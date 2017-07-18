package com.example.karan.traffikill.models;

public class FacebookUser {
    private String name;
    private String UID;

    public FacebookUser(String name, String UID) {
        this.name = name;
        this.UID = UID;
    }

    public FacebookUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
