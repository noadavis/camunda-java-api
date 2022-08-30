package org.example.camunda.entity;

import org.example.camunda.helpers.TaskStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NamedEntityGraph(name = "fetch-without-history", attributeNodes = {
        @NamedAttributeNode("id"),
        @NamedAttributeNode("status"),
        @NamedAttributeNode("dateCreated")
})
@Entity
@Table(name = "custom_task")
public class CustomTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @Column(name = "date_created", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @OneToMany(mappedBy = "customTask", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<TaskHistory> history;
    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    boolean closed;
    @Column(name = "process_instance", length = 40)
    private String processInstanceId;
    private String processKey;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<TaskHistory> getHistory() {
        return history;
    }

    public void setHistory(List<TaskHistory> history) {
        this.history = history;
    }

    public void addHistory(TaskHistory history) {
        if (this.history == null) {
            this.history = new ArrayList<>();
        }
        this.history.add(history);
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }
}
