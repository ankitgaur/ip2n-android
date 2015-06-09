package com.ip2n.mobile.models;

import java.util.List;

/**
 * Created by kritika_pathak on 4/9/2015.
 */
public class Question {
    private String question;
    private List<String> options;
    private int order;
    private boolean required;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }



}
