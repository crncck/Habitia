package com.example.habitapp;

import java.io.Serializable;

public class Habit implements Serializable {

    private String id;
    private String name;
    private String picId;
    private String description;
    private String done;


    public Habit(String id, String name, String picId, String description, String done) {
        this.id = id;
        this.name = name;
        this.picId = picId;
        this.description = description;
        this.done = done;
    }

    public String getId() { return id; }

    public boolean isDone() { return done.equals("true"); }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicId() {
        return picId;
    }

    public String getDescription() { return description; }

}
