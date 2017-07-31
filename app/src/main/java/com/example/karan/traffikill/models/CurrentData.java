package com.example.karan.traffikill.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Karan on 22-07-2017.
 */

public class CurrentData implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CurrentData createFromParcel(Parcel in) {
            return new CurrentData(in);
        }

        public CurrentData[] newArray(int size) {
            return new CurrentData[size];
        }
    };
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("precipIntensity")
    @Expose
    private String precipIntensity;
    @SerializedName("precipProbability")
    @Expose
    private String precipProbability;
    @SerializedName("humidity")
    @Expose
    private String humidity;
    @SerializedName("dewPoint")
    @Expose
    private String dewPoint;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("temperature")
    @Expose
    private double temperature;

    public CurrentData(Parcel in) {
        String[] data = new String[8];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.time = data[0];
        this.summary = data[1];
        this.precipIntensity = data[2];
        this.precipProbability = data[3];
        this.humidity = data[4];
        this.dewPoint = data[5];
        this.icon = data[6];
        this.temperature = Double.parseDouble(data[7]);
    }

    public String getIcon() {
        return icon;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getTime() {
        return time;
    }

    public String getSummary() {
        return summary;
    }

    public String getPrecipIntensity() {
        return precipIntensity;
    }

    public String getPrecipProbability() {
        return precipProbability;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getDewPoint() {
        return dewPoint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.time,
                this.summary,
                this.precipIntensity,
                this.precipProbability,
                this.humidity,
                this.dewPoint,
                this.icon,
                this.temperature + ""});
    }
}
