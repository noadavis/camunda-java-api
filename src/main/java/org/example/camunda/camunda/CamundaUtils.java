package org.example.camunda.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.example.camunda.helpers.Utils;

public class CamundaUtils {
    public static int getTaskId(DelegateExecution delegateExecution) {
        if (delegateExecution.hasVariable("task_id")) {
            String taskId = (String) delegateExecution.getVariable("task_id");
            if (taskId != null) {
                return Utils.checkInt(taskId);
            }
        }
        return 0;
    }
}
