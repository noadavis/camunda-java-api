package org.example.camunda.helpers;

public class JsonAnswer {
    boolean error;
    String answer, description;
    public JsonAnswer(String answer, boolean error, String description) {
        this.error = error;
        this.answer = answer;
        this.description = description;
    }
}
