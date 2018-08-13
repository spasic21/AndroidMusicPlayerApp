package com.example.aspas.songplayerapp;

import android.graphics.Bitmap;

/**
 * Created by aspas on 11/29/2017.
 */

/**
 * Image item that is going to be used to as a playlist cover.
 */
public class ImageItem {
    private Bitmap image;
    private String title;

    public ImageItem(Bitmap image, String title){
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage(){
        return image;
    }

    public void setImage(Bitmap image){
        this.image = image;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }
}
