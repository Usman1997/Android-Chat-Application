package com.example.user.chatapplication.models;

public class Conv {
    public boolean seen;
    public long timestamp;
    public String key;

    public Conv(){

    }
    public Conv(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public Conv WithId(String key){
        this.key = key;
        return this;
    }
}
