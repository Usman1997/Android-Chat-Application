package com.example.user.chatapplication.models;

public class Request {

    String RequestType;
    public  String UserID;

    public Request(){

    }
    public Request(String requestType) {
        RequestType = requestType;
    }

    public String getRequestType() {
        return RequestType;
    }

    public void setRequestType(String requestType) {
        RequestType = requestType;
    }

    public Request WithId(String UserID){
        this.UserID = UserID;
        return this;
    }
}
