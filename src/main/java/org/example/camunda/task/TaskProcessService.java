package org.example.camunda.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.StartFormData;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.example.camunda.camunda.EngineProvider;
import org.example.camunda.entity.CustomTask;
import org.example.camunda.entity.TaskDictionary;
import org.example.camunda.entity.TaskHistory;
import org.example.camunda.entity.User;
import org.example.camunda.helpers.CamundaFormInfo;
import org.example.camunda.helpers.TaskFormVariable;
import org.example.camunda.helpers.TaskStatus;
import org.example.camunda.repository.DictionaryRepository;
import org.example.camunda.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskProcessService {
    private static final Log log = LogFactory.getLog(TaskProcessService.class);

    private final EngineProvider engineProvider;
    private final TaskRepository taskRepository;
    private final DictionaryRepository dictionaryRepository;
    public TaskProcessService(EngineProvider engineProvider, TaskRepository taskRepository, DictionaryRepository dictionaryRepository) {
        this.engineProvider = engineProvider;
        this.taskRepository = taskRepository;
        this.dictionaryRepository = dictionaryRepository;
    }

    public StartFormData getStartFormData(String processKey) {
        ProcessEngine engine = engineProvider.getDefaultProcessEngine();
        ProcessDefinition procDef = engine.getRepositoryService()
                .createProcessDefinitionQuery().processDefinitionKey(processKey).latestVersion().singleResult();
        return engine.getFormService().getStartFormData(procDef.getId());
    }

    public CamundaFormInfo getFormData(String processInstanceId) {
        ProcessEngine engine = engineProvider.getDefaultProcessEngine();
        List<Task> taskList = engine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).list();
        if (taskList != null && taskList.size() > 0) {
            return new CamundaFormInfo(
                    taskList.get(0).getName(),
                    engine.getFormService().getTaskFormData(taskList.get(0).getId())
            );
        }
        return null;
    }

    public CamundaFormInfo getFormInfo(int userTaskId) {
        Optional<CustomTask> optionalTask = taskRepository.findById(userTaskId);
        if (optionalTask.isPresent()) {
            ProcessEngine engine = engineProvider.getDefaultProcessEngine();
            List<Task> taskList = engine.getTaskService().createTaskQuery().processInstanceId(optionalTask.get().getProcessInstanceId()).list();
            if (taskList != null && taskList.size() > 0) {
                Task t = taskList.get(0);
                CamundaFormInfo info = new CamundaFormInfo();
                info.setStepName(t.getName());
                info.setProcessInstance(t.getProcessInstanceId());
                info.setProcessDefinition(t.getProcessDefinitionId());
                info.setTaskId(t.getId());
                info.setFormVariables(null);
                return info;
            }
        }
        return null;
    }

    public List<TaskFormVariable> getAllVariables(String processInstanceId) {
        List<TaskFormVariable> variables = new ArrayList<>();
        ProcessEngine engine = engineProvider.getDefaultProcessEngine();
        Map<String, Object> processVariables = engine.getRuntimeService().getVariables(processInstanceId);
        for (Map.Entry<String, Object> entry : processVariables.entrySet()) {
            variables.add(new TaskFormVariable(entry.getKey(), entry.getValue().toString()));
        }
        return variables;
    }

    public String startUserProcess(User user, String processKey, Map<String, Object> json) {
        ProcessEngine engine = engineProvider.getDefaultProcessEngine();
        ProcessDefinition procDef = engine.getRepositoryService()
                .createProcessDefinitionQuery().processDefinitionKey(processKey).latestVersion().singleResult();
        StartFormData data = engine.getFormService().getStartFormData(procDef.getId());
        Map<String, Object> variables = getProcessVariables(data.getFormFields(), json);
        try {
            CustomTask t = new CustomTask();
            t.setProcessKey(procDef.getId());
            t.setStatus(TaskStatus.PREPARE);
            t.setClosed(false);
            t.setDateCreated(new Date());

            TaskHistory h = new TaskHistory();
            h.setUser(user.getId());
            h.setUsername(user.getUsername());
            h.setCustomTask(t);
            h.setDateCreated(new Date());
            h.setMessage("task created");

            t.addHistory(h);
            taskRepository.save(t);

            variables.put("task_id", String.valueOf(t.getId()));
            ProcessInstance processInstance = engine.getRuntimeService()
                    .startProcessInstanceById(procDef.getId(), variables);

            t.setProcessInstanceId(processInstance.getProcessInstanceId());
            taskRepository.save(t);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String updateUserProcess(User user, int userTaskId, Map<String, Object> json) {
        Optional<CustomTask> optionalTask = taskRepository.findById(userTaskId);
        if (optionalTask.isPresent()) {
            CustomTask t = optionalTask.get();
            ProcessEngine engine = engineProvider.getDefaultProcessEngine();
            List<Task> taskList = engine.getTaskService().createTaskQuery().processInstanceId(t.getProcessInstanceId()).list();
            if (taskList != null && taskList.size() > 0) {
                Task task = taskList.get(0);
                TaskFormData taskFormData = engine.getFormService().getTaskFormData(task.getId());
                TaskHistory h = new TaskHistory();
                h.setUser(user.getId());
                h.setUsername(user.getUsername());
                h.setCustomTask(t);
                h.setDateCreated(new Date());
                h.setMessage("task updated");
                t.addHistory(h);
                taskRepository.save(t);
                if (json != null) {
                    Map<String, Object> variables = getProcessVariables(taskFormData.getFormFields(), json);
                    engine.getTaskService().complete(task.getId(), variables);
                } else {
                    engine.getTaskService().complete(task.getId());
                }
            } else return "process instance task not found";
        } else return "user task not found";
        return null;
    }



    private Map<String, Object> getProcessVariables(List<FormField> fields, Map<String, Object> json) {
        Map<String, Object> vars = new HashMap<>();
        for (FormField f: fields) {
            if (isWritable(f)) {
                if (json.containsKey(f.getId())) {
                    vars.put(f.getId(), json.get(f.getId()));
                } else {
                    log.error("form field not found: " + f.getId());
                }
            }
        }
        return vars;
    }

    public List<TaskFormVariable> getFormVariables(List<FormField> fields) {
        List<TaskFormVariable> list = new ArrayList<>();
        for (FormField f: fields) {
            Object value = f.getValue().getValue();
            TaskFormVariable fv = new TaskFormVariable(
                    f.getId(),
                    f.getLabel(),
                    f.getTypeName(),
                    value == null ? "" : value.toString(),
                    isWritable(f)
            );
            if ("enum".equals(f.getTypeName())) {
                EnumFormType enumType = (EnumFormType) f.getType();
                if (enumType.getValues() != null) {
                    fv.setValues(enumType.getValues());
                }
            } else if ("dictionary".equals(f.getTypeName())) {
                Map<String, String> values = new LinkedHashMap<>();
                List<TaskDictionary> dl = dictionaryRepository.getDictionaryList(f.getId());
                for (TaskDictionary d: dl) {
                    values.put(d.getName(), d.getValue());
                }
                fv.setValues(values);
            }

            list.add(fv);
        }
        return list;
    }

    private boolean isWritable(FormField f) {
        Map<String, String> properties = f.getProperties();
        if (properties != null && properties.size() > 0) {
            if (properties.containsKey("writable")) {
                return "true".equalsIgnoreCase(properties.get("writable"));
            }
        }
        return false;
    }

}
