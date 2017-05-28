package com.ip2n.mobile.models;

import android.graphics.Bitmap;
import android.util.Log;

import java.text.SimpleDateFormat;

/**
 * Created by kritika_pathak on 4/8/2015.
 */
public class Incident implements Comparable<Incident>{

    private String id;
    private String image;
    private String description;
    private String type;
    private String state;
    private String govt;
    private String questions;
    private String status;
    private String reportDate;
    private String createdBy;
    private String createdOn;
    private Bitmap bitmap;
    private long created;

    @Override
    public int compareTo(Incident i) {

        // descending order (ascending order would be:
        // o1.getGrade()-o2.getGrade())
        long diff = i.getCreated() - this.getCreated();

        if(diff>0L){
            //Log.i("Kritika sorted : ",i.getCreated()+":"+this.getCreated()+":"+"1");
            return 1;

        }
        if(diff<0L){
           // Log.i("Kritika sorted : ",i.getCreated()+":"+this.getCreated()+":"+"-1");
            return -1;
        }

        //Log.i("Kritika sorted : ",i.getCreated()+":"+this.getCreated()+":"+"0");
        return 0;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGovt() {
        return govt;
    }

    public void setGovt(String govt) {
        this.govt = govt;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Incident incident = (Incident) o;

        return id.equals(incident.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
