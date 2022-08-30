package org.example.camunda.camunda.delegate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.camunda.camunda.CamundaUtils;
import org.springframework.stereotype.Component;

@Component
public class SendNotify implements JavaDelegate {
    private static final Log log = LogFactory.getLog(SendNotify.class);
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String processInstanceId = delegateExecution.getProcessInstanceId();
        int taskId = CamundaUtils.getTaskId(delegateExecution);
        if (taskId > 0) {
            log.info(String.format("SendNotify: task_id: %d", taskId));
        } else log.error(String.format("SendNotify delegate: task_id: not found, ProcessInstanceId: %s", processInstanceId));
    }
}
