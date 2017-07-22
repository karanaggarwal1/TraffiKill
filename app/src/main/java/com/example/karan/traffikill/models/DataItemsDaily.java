package com.example.karan.traffikill.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Karan on 22-07-2017.
 */

class DataItemsDaily implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataItemsDaily createFromParcel(Parcel in) {
            return new DataItemsDaily(in);
        }

        public DataItemsDaily[] newArray(int size) {
            return new DataItemsDaily[size];
        }
    };
    @SerializedName("summary")
    @Expose
    public String summary;
    @SerializedName("precipIntensity")
    @Expose
    public String precipIntensity;
    @SerializedName("precipIntensityMax")
    @Expose
    public String precipIntensityMax;
    @SerializedName("precipIntensityMaxTime")
    @Expose
    public String precipIntensityMaxTime;
    @SerializedName("precipProbability")
    @Expose
    public String precipProbability;
    @SerializedName("humidity")
    @Expose
    public String humidity;

    public DataItemsDaily(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.summary = data[0];
        this.precipIntensity = data[1];
        this.precipIntensityMax = data[2];
        this.precipIntensityMaxTime = data[3];
        this.precipProbability = data[4];
        this.humidity = data[5];
    }

    public String getSummary() {
        return summary;
    }

    public String getPrecipIntensity() {
        return precipIntensity;
    }

    public String getPrecipIntensityMax() {
        return precipIntensityMax;
    }

    public String getPrecipIntensityMaxTime() {
        return precipIntensityMaxTime;
    }

    public String getPrecipProbability() {
        return precipProbability;
    }

    public String getHumidity() {
        return humidity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.summary,
                this.precipIntensity,
                this.precipIntensityMax,
                this.precipIntensityMaxTime,
                this.precipProbability,
                this.humidity});
    }
}
