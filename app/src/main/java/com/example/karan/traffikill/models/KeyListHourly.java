package com.example.karan.traffikill.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Karan on 22-07-2017.
 */

public class KeyListHourly implements Parcelable {

    public static final Parcelable.Creator<KeyListHourly> CREATOR =
            new Parcelable.Creator<KeyListHourly>() {
                public KeyListHourly createFromParcel(Parcel in) {
                    return new KeyListHourly(in);
                }

                public KeyListHourly[] newArray(int size) {
                    return new KeyListHourly[size];
                }
            };
    @SerializedName("summary")
    @Expose
    public String summary;
    @SerializedName("data")
    @Expose
    public ArrayList<CurrentData> data;

    public KeyListHourly(Parcel parcel) {
        this.summary = parcel.readString();
        parcel.readTypedList(this.data, CurrentData.CREATOR);
    }

    public String getSummary() {
        return summary;
    }

    public ArrayList<CurrentData> getData() {
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.summary);
        dest.writeTypedList(this.data);
    }
}
