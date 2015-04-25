package com.ip2n.mobile.models;

import java.util.List;

/**
 * Created by kritika_pathak on 4/9/2015.
 */
public class Question {
    private String name;
    private List<String> options;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
