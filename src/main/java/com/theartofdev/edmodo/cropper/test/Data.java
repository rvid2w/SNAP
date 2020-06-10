package com.theartofdev.edmodo.cropper.test;

import android.media.Image;
import android.net.Uri;

import java.net.URI;

public class Data {
    private String Tag;
    private byte[] imageURI;
    private Uri image;
    private int ID;
    Data(){}
    Data(String tag, byte[] image){
        this.Tag =tag;
        this.imageURI = image;
    }

    public byte[] getImage() {
        return imageURI;
    }

    public void setImage(byte[] image) {
        this.imageURI = image;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }
    public void setImageURI(Uri uri){
        this.image = uri;
    }
    public Uri getImageURI(){
        return this.image;
    }
    public void setID(int id){
        this.ID = id;
    }

    public int getID() {
        return ID;
    }
}
