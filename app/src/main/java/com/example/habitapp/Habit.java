package com.example.habitapp;

import java.io.Serializable;

public class Habit implements Serializable {

    private String id;
    private String name;
    private String picId;
    private String description;
    private String done;
    private String target;
    private String value;
    private String type;
    private String done_percent;

    public Habit(String id, String name, String picId, String description, String done, String target, String value, String type, String done_percent) {
        this.id = id;
        this.name = name;
        this.picId = picId;
        this.description = description;
        this.done = done;
        this.target = target;
        this.value = value;
        this.type = type;
        this.done_percent = done_percent;
    }

    public String getDone_percent() { return done_percent; }

    public String getTarget() { return target; }

    public String getValue() { return value; }

    public String getType() { return type; }

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
