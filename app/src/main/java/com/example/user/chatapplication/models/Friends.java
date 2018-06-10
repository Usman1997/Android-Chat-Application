package com.example.user.chatapplication.models;

public class Friends {

    String date;
    public String User_id;
public Friends(){

}
    public Friends(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Friends WithId(String id){
        this.User_id = id;
        return this;
    }
}
