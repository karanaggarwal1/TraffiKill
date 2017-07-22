package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Karan on 22-07-2017.
 */

public class KeyListDaily {
    @SerializedName("summary")
    @Expose
    public String summary;

    @SerializedName("data")
    @Expose
    public ArrayList<DataItemsDaily> data;

    public String getSummary() {
        return summary;
    }

    public ArrayList<DataItemsDaily> getData() {
        return data;
    }
}
