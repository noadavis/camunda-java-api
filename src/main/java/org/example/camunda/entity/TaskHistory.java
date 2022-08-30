package org.example.camunda.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "task_history")
public class TaskHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name="custom_task_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private CustomTask customTask;
    @Column(name = "date_created", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    private int user;
    private String username;
    @Column(length = 1500)
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CustomTask getCustomTask() {
        return customTask;
    }

    public void setCustomTask(CustomTask customTask) {
        this.customTask = customTask;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
