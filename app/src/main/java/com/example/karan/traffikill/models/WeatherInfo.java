package com.example.karan.traffikill.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Karan on 22-07-2017.
 */

public class WeatherInfo implements Parcelable {
    public static final Parcelable.Creator<WeatherInfo> CREATOR =
            new Parcelable.Creator<WeatherInfo>() {
                public WeatherInfo createFromParcel(Parcel in) {
                    return new WeatherInfo(in);
                }

                public WeatherInfo[] newArray(int size) {
                    return new WeatherInfo[size];
                }
            };

    @SerializedName("currently")
    @Expose
    ArrayList<CurrentData> currently;

    @SerializedName("hourly")
    @Expose
    ArrayList<KeyListHourly> hourly;

    @SerializedName("daily")
    @Expose
    ArrayList<KeyListDaily> daily;

    public WeatherInfo(Parcel parcel) {
        parcel.readTypedList(this.currently, CurrentData.CREATOR);
        parcel.readTypedList(this.hourly, KeyListHourly.CREATOR);
        parcel.readTypedList(this.daily, KeyListDaily.CREATOR);
    }

    public ArrayList<CurrentData> getCurrently() {
        return currently;
    }

    public ArrayList<KeyListHourly> getHourly() {
        return hourly;
    }

    public ArrayList<KeyListDaily> getDaily() {
        return daily;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.currently);
        dest.writeTypedList(this.hourly);
        dest.writeTypedList(this.daily);
    }
}
