package com.example.habitapp;

import java.io.Serializable;

public class FotoGallery implements Serializable {

    private int picId;

    public FotoGallery(int picId){
        this.picId = picId;
    }

    public int getPicId() {
        return picId;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }
}
