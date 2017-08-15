package com.example.karan.traffikill.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultData implements Parcelable {
    public static final Parcelable.Creator<ResultData> CREATOR =
            new Parcelable.Creator<ResultData>() {
                public ResultData createFromParcel(Parcel in) {
                    return new ResultData(in);
                }

                public ResultData[] newArray(int size) {
                    return new ResultData[size];
                }
            };

    private ResultData(Parcel parcel) {
        this.name = parcel.readString();
        this.rating = parcel.readFloat();
        this.geometry = parcel.readParcelable(GeometryData.class.getClassLoader());
        parcel.readTypedList(this.photos, PhotoDetails.CREATOR);
    }
    @SerializedName("geometry")
    @Expose
    private GeometryData geometry;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("rating")
    @Expose
    private float rating;

    @SerializedName("vicinty")
    @Expose
    private String vicinity;

    @SerializedName("photos")
    @Expose
    private ArrayList<PhotoDetails> photos;

    public ArrayList<PhotoDetails> getPhotosData() {
        return photos;
    }

    public GeometryData getGeometry() {
        return geometry;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeParcelable(this.geometry, flags);
        dest.writeFloat(this.rating);
        dest.writeTypedList(this.photos);
    }
}
