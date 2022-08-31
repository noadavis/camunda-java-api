package org.example.camunda.camunda.delegate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.camunda.entity.CustomTask;
import org.example.camunda.entity.TaskHistory;
import org.example.camunda.repository.TaskRepository;
import org.example.camunda.helpers.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.example.camunda.camunda.CamundaUtils;

import java.util.Date;
import java.util.Optional;

@Component
public class ChangeStatus implements JavaDelegate {
    private static final Log log = LogFactory.getLog(ChangeStatus.class);
    private Expression status;
    @Autowired
    TaskRepository taskRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String processInstanceId = delegateExecution.getProcessInstanceId();
        int taskId = CamundaUtils.getTaskId(delegateExecution);
        if (taskId > 0) {
            log.info(String.format("ChangeStatus: task_id: %d, ProcessInstanceId: %s", taskId, delegateExecution.getId()));
            Optional<CustomTask> optionalTask = taskRepository.findById(taskId);
            if (optionalTask.isPresent()) {
                CustomTask t = optionalTask.get();
                TaskStatus taskStatus = getTaskStatus(delegateExecution, taskId);
                if (taskStatus != null) {
                    t.setStatus(taskStatus);
                    TaskHistory h = new TaskHistory();
                    h.setUser(0);
                    h.setUsername("system");
                    h.setCustomTask(t);
                    h.setDateCreated(new Date());
                    h.setMessage("status updated: " + taskStatus.name());
                    t.addHistory(h);
                    if (taskStatus.equals(TaskStatus.CLOSE)) {
                        t.setClosed(true);
                    }
                    taskRepository.save(t);
                } else log.error(String.format("ChangeStatus delegate: new TaskStatus not found, taskId: %d", taskId));
            } else log.error(String.format("ChangeStatus delegate: CustomTask not found, taskId: %d", taskId));
        } else log.error(String.format("ChangeStatus delegate: task_id: not found, ProcessInstanceId: %s", processInstanceId));
    }

    private TaskStatus getTaskStatus(DelegateExecution delegateExecution, int taskId) {
        TaskStatus taskStatus = null;
        try {
            String statusValue = (String) status.getValue(delegateExecution);
            statusValue = statusValue.toUpperCase();
            log.info(String.format("ChangeStatus delegate [status : %s] [taskId: %d]", statusValue, taskId));
            taskStatus = TaskStatus.valueOf(statusValue);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return taskStatus;
    }
}
