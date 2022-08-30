package org.example.camunda.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Task;
import org.example.camunda.app.appdata.AppData;
import org.example.camunda.camunda.EngineProvider;
import org.example.camunda.entity.CustomTask;
import org.example.camunda.entity.ProcessDef;
import org.example.camunda.entity.TaskHistory;
import org.example.camunda.entity.User;
import org.example.camunda.helpers.Utils;
import org.example.camunda.repository.ProcessRepository;
import org.example.camunda.repository.TaskRepository;
import org.example.camunda.helpers.TaskStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("bpmn")
public class BpmnController {

    @Value("${server.port}")
    private int port;
    @Value("${spring.application.process-key-default}")
    private String defaultProcessKey;

    private final ApplicationContext context;
    private final ProcessRepository processRepository;
    private final TaskRepository taskRepository;
    public BpmnController(ApplicationContext context, ProcessRepository processRepository, TaskRepository taskRepository) {
        this.context = context;
        this.processRepository = processRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/task")
    public String tasks(Authentication authentication, Model model) {
        AppData appData = context.getBean(AppData.class, authentication, "Camunda task list", "tasks", "");
        model.addAttribute("appData", appData);
        EngineProvider engineProvider = context.getBean(EngineProvider.class);
        ProcessEngine engine = engineProvider.getDefaultProcessEngine();
        List<Task> list = engine.getTaskService().createTaskQuery().active().list();
        model.addAttribute("tasks", list);
        model.addAttribute("withEngine", true);
        return "camunda/task-list";
    }

    @GetMapping("/task/{id}")
    public String taskInfo(
            @PathVariable(value = "id") String id,
            Authentication authentication, Model model) {
        AppData appData = context.getBean(AppData.class, authentication, "Camunda task", "tasks", "");
        model.addAttribute("appData", appData);
        Task task = getCamundaTask(id);
        ProcessDef process = processRepository.getByProcessId(task.getProcessDefinitionId());
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        model.addAttribute("taskInfo", gson.toJson(task));
        model.addAttribute("processId", process.getId());
        model.addAttribute("taskId", task.getId());
        return "camunda/task-info";
    }

    @GetMapping(value = "/task/json/{id}", produces = "application/json")
    @ResponseBody
    public String taskJsonInfo(
            @PathVariable(value = "id") String id,
            Authentication authentication, Model model) {
        Task task = getCamundaTask(id);
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        return gson.toJson(task);
    }

    private Task getCamundaTask(String camundaTaskId) {
        EngineProvider engineProvider = context.getBean(EngineProvider.class);
        ProcessEngine engine = engineProvider.getDefaultProcessEngine();
        return engine.getTaskService().createTaskQuery().active().taskId(camundaTaskId).singleResult();
    }

    @GetMapping("/process/{process}")
    public String info(
            @PathVariable(value = "process") ProcessDef process,
            Authentication authentication, Model model) {
        AppData appData = context.getBean(AppData.class, authentication, "Process", "server", "");
        model.addAttribute("appData", appData);
        model.addAttribute("bpmn", process);
        model.addAttribute("applicationUrl", String.format("http://127.0.0.1:%d", port));
        return "camunda/process-info";
    }

    @GetMapping("/process/key/{process}")
    public String infoByKey(
            @PathVariable(value = "process") String processKey,
            Authentication authentication, Model model) {
        AppData appData = context.getBean(AppData.class, authentication, "Process", "server", "");
        model.addAttribute("appData", appData);
        List<ProcessDef> process = processRepository.getByProcessKey(processKey);
        model.addAttribute("bpmn", process.get(0));
        model.addAttribute("applicationUrl", String.format("http://127.0.0.1:%d", port));
        return "camunda/process-info";
    }

    @GetMapping(value = "/process/{process}/xml", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<?> getXml(@PathVariable(value = "process") ProcessDef process) {
        String xml = getProcessXml(process);
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", MediaType.APPLICATION_XML_VALUE);
        headers.set("Content-Disposition","attachment; filename=\""+ timeStamp +".xml\"");
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(xml.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping(value = "/process/{process}/json", produces = "application/json")
    @ResponseBody
    public String getXmlJson(@PathVariable(value = "process") ProcessDef process) {
        String xml = getProcessXml(process);
        return String.format("{ \"bpmn20Xml\": %s }", new Gson().toJson(xml));
    }

    private String getProcessXml(ProcessDef process) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><bpmn>not found</bpmn>";
        if (process != null) {
            EngineProvider engineProvider = context.getBean(EngineProvider.class);
            ProcessEngine engine = engineProvider.getDefaultProcessEngine();
            InputStream is = engine.getRepositoryService().getProcessModel(process.getProcessId());
            try {
                xml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return xml;
    }

    @GetMapping("/process")
    public String list(Authentication authentication, Model model) {
        AppData appData = context.getBean(AppData.class, authentication, "Uploaded process list", "server", "");
        model.addAttribute("appData", appData);
        model.addAttribute("processDefault", defaultProcessKey);
        model.addAttribute("processList", processRepository.getProcessListWithGroup());
        return "camunda/process-list";
    }

    @GetMapping("/process/new")
    public String newBpmn(Authentication authentication, Model model) {
        AppData appData = context.getBean(AppData.class, authentication, "Upload new process", "server", "");
        model.addAttribute("appData", appData);
        return "camunda/process-new";
    }

    public static File multipartToFile(MultipartFile multipart) {
        File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+multipart.getOriginalFilename());
        try {
            multipart.transferTo(convFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convFile;
    }

    @PostMapping(value = "/process-upload", produces="application/json")
    @ResponseBody
    public HashMap<String, Object> uploadProcess(@RequestParam(value = "bpmn") MultipartFile bpmnFile) {
        String description = "unknown error";
        boolean error = true;
        if (bpmnFile != null) {

            EngineProvider engineProvider = context.getBean(EngineProvider.class);
            ProcessEngine engine = engineProvider.getDefaultProcessEngine();
            try {
                DeploymentBuilder deploymentBuilder = engine.getRepositoryService().createDeployment();
                Deployment deployment = deploymentBuilder.addInputStream(
                        bpmnFile.getName(), bpmnFile.getInputStream()
                ).deploy();
                DeploymentEntity de = (DeploymentEntity) deployment;
                ProcessDefinition processDefinition = de.getDeployedProcessDefinitions().get(0);

                ProcessDef process = new ProcessDef();
                process.setProcessId(processDefinition.getId());
                process.setProcessKey(processDefinition.getKey());
                process.setName(processDefinition.getName());
                process.setVersion(processDefinition.getVersion());
                process.setDeploymentId(deployment.getId());
                process.setResource(processDefinition.getResourceName());
                process.setDeploymentTime(de.getDeploymentTime());
                processRepository.save(process);

                error = false;
            } catch (Exception e) {
                e.printStackTrace();
                description = e.getMessage();
            }

        } else description = "upload error";
        return Utils.getAnswer(error ? "": "ok", error, description);
    }


    @GetMapping(value = "/process-check", produces="application/json")
    @ResponseBody
    public HashMap<String, Object> checkLoaded() {
        EngineProvider engineProvider = context.getBean(EngineProvider.class);
        ProcessEngine engine = engineProvider.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().active().list();
        List<ProcessDef> processList = new ArrayList<>();
        for (ProcessDefinition bpmn : processDefinitions) {
            ProcessDef findProcess = processRepository.getByProcessId(bpmn.getId());
            if (findProcess != null) break;

            ProcessDef processDef = new ProcessDef();
            processDef.setProcessId(bpmn.getId());
            processDef.setProcessKey(bpmn.getKey());
            processDef.setName(bpmn.getName());
            processDef.setVersion(bpmn.getVersion());
            processDef.setDeploymentId(bpmn.getDeploymentId());
            processDef.setResource(bpmn.getResourceName());

            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(bpmn.getDeploymentId()).singleResult();
            processDef.setDeploymentTime(deployment.getDeploymentTime());

            processList.add(processDef);
        }
        if (processList.size() > 0) {
            processRepository.saveAll(processList);
        }
        return Utils.getAnswer("ok", false, "");
    }

    @PostMapping(value = "/api/process-delete", produces="application/json")
    @ResponseBody
    public HashMap<String, Object> deleteProcess(
            Authentication authentication,
            @RequestParam(value = "procInstId", defaultValue = "") String procInstId) {
        String description = "unknown error";
        boolean error = true;
        if (!"".equals(procInstId)) {
            EngineProvider engineProvider = context.getBean(EngineProvider.class);
            ProcessEngine engine = engineProvider.getDefaultProcessEngine();
            Object var = engine.getRuntimeService().getVariable(procInstId, "task_id");
            if (var != null) {
                int taskId = Utils.checkInt(var.toString());
                if (taskId > 0) {
                    Optional<CustomTask> optionalTask = taskRepository.findById(taskId);
                    if (optionalTask.isPresent()) {
                        engine.getRuntimeService().deleteProcessInstanceIfExists(procInstId, "user", true, false, true, true);

                        User user = Utils.getCurrentUser(authentication);
                        CustomTask t = optionalTask.get();
                        t.setClosed(true);
                        t.setStatus(TaskStatus.CLOSE);
                        TaskHistory h = new TaskHistory();
                        h.setDateCreated(new Date());
                        h.setCustomTask(t);
                        h.setMessage("camunda task close");
                        h.setUser(user.getId());
                        h.setUsername(user.getUsername());
                        t.addHistory(h);
                        taskRepository.save(t);

                        error = false;
                    }
                } else description = "task_id not found";
            } else description = "task_id not found";
        } else {
            description = "request error";
        }
        return Utils.getAnswer(error ? "": "ok", error, description);
    }
}
