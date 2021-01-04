package com.example.habitapp;

import java.io.Serializable;

public class Habit implements Serializable {

    private String name;
    private String picId;
    private String description;
    private String done;


    public Habit(String name, String picId, String description, String done) {
        this.name = name;
        this.picId = picId;
        this.description = description;
        this.done = done;
    }

    public String getDone() { return done; }

    public void setDone(String done) { this.done = done; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}
