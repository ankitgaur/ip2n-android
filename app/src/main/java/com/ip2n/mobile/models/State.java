package com.ip2n.mobile.models;

import java.util.ArrayList;

/**
 * Created by ankit on 4/26/15.
 */
public class State {
    private String id;
    private String name;
    private String currGovt;
    private ArrayList<String> govts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrGovt() {
        return currGovt;
    }

    public void setCurrGovt(String currGovt) {
        this.currGovt = currGovt;
    }

    public ArrayList<String> getGovts() {
        return govts;
    }

    public void setGovts(ArrayList<String> govts) {
        this.govts = govts;
    }
}
