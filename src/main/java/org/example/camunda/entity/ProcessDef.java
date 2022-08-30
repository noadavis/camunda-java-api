package org.example.camunda.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bpmn_process")
public class ProcessDef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "process_id")
    private String processId;
    @Column(name = "process_key")
    private String processKey;
    private String name;
    private int version;
    @Column(name = "deployment_id")
    private String deploymentId;
    private String resource;
    @Column(name = "deployment_time", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deploymentTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Date getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(Date deploymentTime) {
        this.deploymentTime = deploymentTime;
    }
}
