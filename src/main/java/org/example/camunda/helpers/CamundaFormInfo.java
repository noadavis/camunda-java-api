package org.example.camunda.helpers;

import org.camunda.bpm.engine.form.TaskFormData;

public class CamundaFormInfo {
    private String processInstance;
    private String processDefinition;
    private String taskId;
    private String stepName;
    private TaskFormData formVariables;

    public CamundaFormInfo() {
    }

    public CamundaFormInfo(String stepName, TaskFormData formVariables) {
        this.stepName = stepName;
        this.formVariables = formVariables;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public TaskFormData getFormVariables() {
        return formVariables;
    }

    public void setFormVariables(TaskFormData formVariables) {
        this.formVariables = formVariables;
    }

    public String getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(String processInstance) {
        this.processInstance = processInstance;
    }

    public String getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(String processDefinition) {
        this.processDefinition = processDefinition;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
