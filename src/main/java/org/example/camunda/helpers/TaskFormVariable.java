package org.example.camunda.helpers;

import java.util.Map;

public class TaskFormVariable {
    private String id;
    private String label;
    private String type;
    private String value;
    private boolean writable;
    private Map<String, String> values;

    public TaskFormVariable() {
    }

    public TaskFormVariable(String id, String label, String type, String value, boolean writable) {
        this.id = id;
        this.label = label;
        this.type = type;
        this.value = value;
        this.writable = writable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }
}
