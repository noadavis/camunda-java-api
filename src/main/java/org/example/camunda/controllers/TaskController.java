package org.example.camunda.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.camunda.bpm.engine.form.StartFormData;
import org.example.camunda.app.appdata.AppData;
import org.example.camunda.entity.CustomTask;
import org.example.camunda.entity.ProcessDef;
import org.example.camunda.helpers.CamundaFormInfo;
import org.example.camunda.helpers.TaskFormVariable;
import org.example.camunda.helpers.Utils;
import org.example.camunda.repository.ProcessRepository;
import org.example.camunda.repository.TaskRepository;
import org.example.camunda.task.TaskProcessService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("task")
public class TaskController {

    @Value("${spring.application.process-key-default}")
    private String defaultProcessKey;

    private final ApplicationContext context;
    private final TaskRepository taskRepository;
    private final ProcessRepository processRepository;
    private final TaskProcessService taskProcessService;
    public TaskController(ApplicationContext context, TaskRepository taskRepository, ProcessRepository processRepository, TaskProcessService taskProcessService) {
        this.context = context;
        this.taskRepository = taskRepository;
        this.processRepository = processRepository;
        this.taskProcessService = taskProcessService;
    }

    @GetMapping(value = "")
    public String taskList(Authentication authentication, Model model) {
        AppData appData = context.getBean(AppData.class, authentication, "User task list", "tasks", "");
        model.addAttribute("appData", appData);
        model.addAttribute("processDefault", defaultProcessKey);
        model.addAttribute("processList", processRepository.getProcessListWithGroup());
        return "task/_table";
    }

    @PostMapping(value = "/list", produces = "application/json")
    @ResponseBody
    public String getTaskList(
            Authentication authentication,
            @RequestParam(value = "all", defaultValue = "") String all,
            @RequestParam(value = "closed", defaultValue = "") String closed,
            @RequestParam(value = "num", defaultValue = "") String num,
            @RequestParam(value = "count", defaultValue = "") String count) {
        int pageNum = Utils.checkInt(num);
        int pageSize = Utils.checkInt(count);
        Page<CustomTask> answer = taskRepository.getCustomTaskList(
                all.equals("true") ? null : closed.equals("true"),
                PageRequest.of(
                        pageNum,
                        pageSize,
                        Sort.by("id").descending()
                )
        );
        if (answer != null) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").disableHtmlEscaping().setPrettyPrinting().create();
            List<CustomTask> content = answer.getContent();
            for (CustomTask c: content) {
                c.setHistory(null);
            }
            String contentArray = content.size() > 0 ? gson.toJson(content) : "[]";
            List<Integer> pages = Utils.getPages(pageNum, answer.getTotalPages());
            return String.format("{ \"error\": false, \"pages\": %s, \"maxPage\": %d, \"currentPage\": %d, \"content\": %s }",
                    gson.toJson(pages),
                    answer.getTotalPages(),
                    pageNum + 1,
                    contentArray
            );
        }
        return "{ \"error\": true }";
    }

    @PostMapping(value = "/new")
    public String newRequest(Authentication authentication, Model model,
                             @RequestParam(value = "processKey", defaultValue = "") String process) {
        String processKey = getProcessKey(process);
        StartFormData data = taskProcessService.getStartFormData(processKey);
        List<TaskFormVariable> formVariables = taskProcessService.getFormVariables(data.getFormFields());

        AppData appData = context.getBean(AppData.class, authentication, "Create user task", "tasks", "");
        model.addAttribute("appData", appData);
        model.addAttribute("fields", formVariables);
        model.addAttribute("processKey", processKey);
        return String.format("task/%s/%s", processKey, data.getFormKey());
    }

    @GetMapping(value = "/update/{id}", produces = "application/json")
    public String createUserProcess(Authentication authentication, Model model, @PathVariable(value = "id") String id) {
        String processKey = "";
        String formKey = "";
        String stepName = "";
        List<TaskFormVariable> formVariables = new ArrayList<>();
        int taskId = Utils.checkInt(id);
        if (taskId > 0) {
            Optional<CustomTask> optionalTask = taskRepository.findById(taskId);
            if (optionalTask.isPresent()) {
                CustomTask t = optionalTask.get();
                CamundaFormInfo formInfo = taskProcessService.getFormData(t.getProcessInstanceId());
                formVariables = taskProcessService.getFormVariables(formInfo.getFormVariables().getFormFields());
                formKey = formInfo.getFormVariables().getFormKey();
                stepName = formInfo.getStepName();
                processKey = t.getProcessKey().split(":")[0];
            }
        }
        AppData appData = context.getBean(AppData.class, authentication, String.format("Update user task [ %s ]", stepName), "tasks", "");
        model.addAttribute("appData", appData);
        model.addAttribute("fields", formVariables);
        model.addAttribute("taskId", taskId);
        return String.format("task/%s/%s", processKey, formKey);
    }



    @PostMapping(value = "/process", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String createUserProcess(Authentication authentication, @RequestBody Map<String, Object> json) {
        String description = null;
        String processKey = json.containsKey("_processKey") ? (String) json.get("_processKey") : "";
        if (!"".equals(processKey)) {
            description = taskProcessService.startUserProcess(Utils.getCurrentUser(authentication), processKey, json);
        } else description = "processKey not found";
        return String.format("{ \"error\": %s, \"description\": \"%s\" }", description != null, description);
    }

    @PostMapping(value = "/update", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String updateUserProcess(Authentication authentication, @RequestBody Map<String, Object> json) {
        String description = null;
        int taskId = json.containsKey("_taskId") ? Utils.checkInt((String) json.get("_taskId")) : -1;
        if (taskId > 0) {
            description = taskProcessService.updateUserProcess(Utils.getCurrentUser(authentication), taskId, json);
        } else description = "taskId not found";
        return String.format("{ \"error\": %s, \"description\": \"%s\" }", description != null, description);
    }



    private String getProcessKey(String process) {
        int processId = Utils.checkInt(process);
        String processKey = defaultProcessKey;
        if (processId > 0) {
            ProcessDef processDef = processRepository.getById(processId);
            if (processDef != null) {
                processKey = processDef.getProcessKey();
            }
        }
        return processKey;
    }
}