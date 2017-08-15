package com.example.karan.traffikill.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PhotoDetails implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PhotoDetails createFromParcel(Parcel in) {
            return new PhotoDetails(in);
        }

        public PhotoDetails[] newArray(int size) {
            return new PhotoDetails[size];
        }
    };
    @SerializedName("photo_reference")
    @Expose
    private String photoReference;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("width")
    @Expose
    private int width;

    private PhotoDetails(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        this.photoReference = data[0];
        this.height = Integer.getInteger(data[1]);
        this.width = Integer.getInteger(data[2]);
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.photoReference,
                this.height + "",
                this.width + ""});
    }
}
