package com.example.habitapp;

import android.os.Parcelable;

import java.io.Serializable;

public class User implements Serializable  {

    private String name;
    private Integer proPicId;

    public User(String name, Integer proPicId){

        this.name = name;
        this.proPicId = proPicId;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProPicId() {
        return proPicId;
    }

    public void setProPicId(Integer proPicId) {
        this.proPicId = proPicId;
    }
}






