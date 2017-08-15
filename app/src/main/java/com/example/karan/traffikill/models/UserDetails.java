package com.example.karan.traffikill.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class UserDetails implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };
    private String name, email;
    private Uri imageURL;

    public UserDetails(String name, String email, Uri imageURL) {
        this.name = name;
        this.email = email;
        this.imageURL = imageURL;
    }

    private UserDetails(Parcel in) {
        String[] data = new String[3];

        in.readStringArray(data);
        this.name = data[0];
        this.email = data[1];
        this.imageURL = Uri.parse(data[2]);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Uri getImageURL() {
        return imageURL;
    }

    public void setImageURL(Uri imageURL) {
        this.imageURL = imageURL;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.name,
                this.email,
                this.imageURL.toString()});
    }
}
