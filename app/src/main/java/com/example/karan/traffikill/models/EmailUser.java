package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Karan on 29-07-2017.
 */

public class EmailUser {
    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("verified")
    @Expose
    private String verified;

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getVerified() {
        return verified;
    }
}
