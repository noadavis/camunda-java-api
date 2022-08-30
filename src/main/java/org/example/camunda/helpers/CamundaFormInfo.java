package org.example.camunda.helpers;

import org.camunda.bpm.engine.form.TaskFormData;

public class CamundaFormInfo {
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
}
