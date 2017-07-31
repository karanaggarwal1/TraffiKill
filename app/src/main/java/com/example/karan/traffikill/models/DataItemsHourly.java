package com.example.karan.traffikill.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Karan on 22-07-2017.
 */

public class DataItemsHourly implements Parcelable {
    //    "dewPoint": 54.24,
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataItemsHourly createFromParcel(Parcel in) {
            return new DataItemsHourly(in);
        }

        public DataItemsHourly[] newArray(int size) {
            return new DataItemsHourly[size];
        }
    };
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("precipIntensity")
    @Expose
    public String precipIntensity;
    @SerializedName("precipProbability")
    @Expose
    public String precipProbability;
    @SerializedName("summary")
    @Expose
    public String summary;
    @SerializedName("humidity")
    @Expose
    public String humidity;

    @SerializedName("dewPoint")
    @Expose
    public double dewPoint;

    @SerializedName("temperature")
    @Expose
    public double temperature;

    public double getTemperature() {
        return temperature;
    }

    public DataItemsHourly(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.time = data[0];
        this.precipIntensity = data[1];
        this.precipProbability = data[2];
        this.summary = data[3];
        this.humidity = data[4];
    }

    public double getDewPoint() {
        return dewPoint;
    }

    public String getTime() {
        return time;
    }

    public String getPrecipIntensity() {
        return precipIntensity;
    }

    public String getPrecipProbability() {
        return precipProbability;
    }

    public String getSummary() {
        return summary;
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
        dest.writeStringArray(new String[]{this.time,
                this.precipIntensity,
                this.precipProbability,
                this.summary,
                this.humidity});
    }
}

