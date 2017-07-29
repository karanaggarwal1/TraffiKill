package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacebookUser {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("uid")
    @Expose
    private String uid;

    @SerializedName("photoURL")
    @Expose
    private String photoURL;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    public String getUid() {
        return uid;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

}
