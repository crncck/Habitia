package com.example.habitapp;

import android.os.Parcelable;

import java.io.Serializable;

public class User implements Serializable, Comparable<User> {

    private String name;
    private String surname;
    private String profileUrl;
    private int done_percent;

    public User(String name, String surname, String profileUrl, int done_percent){

        this.name = name;
        this.surname = surname;
        this.profileUrl = profileUrl;
        this.done_percent = done_percent;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public String getProfileUrl() { return profileUrl; }

    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }

    public int getDone_percent() { return done_percent; }

    public void setDone_percent(int done_percent) { this.done_percent = done_percent; }

    @Override
    public int compareTo(User o) {
        return this.getDone_percent() - o.getDone_percent();
    }
}






