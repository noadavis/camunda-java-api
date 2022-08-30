package org.example.camunda.camunda.delegate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.camunda.camunda.CamundaUtils;
import org.example.camunda.entity.CustomTask;
import org.example.camunda.entity.TaskHistory;
import org.example.camunda.helpers.TaskStatus;
import org.example.camunda.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class TaskEndEvent implements JavaDelegate {
    private static final Log log = LogFactory.getLog(TaskEndEvent.class);
    @Autowired
    TaskRepository taskRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String processInstanceId = delegateExecution.getProcessInstanceId();
        int taskId = CamundaUtils.getTaskId(delegateExecution);
        if (taskId > 0) {
            log.info(String.format("TaskEndEvent: task_id: %d", taskId));
            Optional<CustomTask> optionalTask = taskRepository.findById(taskId);
            if (optionalTask.isPresent()) {
                CustomTask t = optionalTask.get();
                t.setStatus(TaskStatus.CONFIRM);
                TaskHistory h = new TaskHistory();
                h.setUser(0);
                h.setUsername("system");
                h.setCustomTask(t);
                h.setDateCreated(new Date());
                h.setMessage("TaskEndEvent: status updated: " + t.getStatus().name());
                t.addHistory(h);
                taskRepository.save(t);
            } else log.error(String.format("TaskEndEvent delegate: CustomTask not found, taskId: %d", taskId));
        } else log.error(String.format("TaskEndEvent delegate: task_id: not found, ProcessInstanceId: %s", processInstanceId));
    }
}