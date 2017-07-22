package com.example.karan.traffikill.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Karan on 22-07-2017.
 */

public class KeyListHourly {

    @SerializedName("summary")
    @Expose
    public String summary;

    @SerializedName("data")
    @Expose
    public ArrayList<DataItemsHourly> data;

    public String getSummary() {
        return summary;
    }

    public ArrayList<DataItemsHourly> getData() {
        return data;
    }
}
