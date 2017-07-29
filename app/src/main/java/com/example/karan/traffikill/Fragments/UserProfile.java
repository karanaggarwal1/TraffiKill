package com.example.karan.traffikill.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karan.traffikill.Activities.LoginActivity;
import com.example.karan.traffikill.R;
import com.example.karan.traffikill.Services.FirebaseDataAPI;
import com.example.karan.traffikill.Views.CircleDrawable;
import com.example.karan.traffikill.models.EmailUser;
import com.example.karan.traffikill.models.FacebookUser;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Karan on 28-07-2017.
 */

public class UserProfile extends Fragment {
    public static final String TAG = "FragmentUserProfile";
    public Context context;
    private FacebookUser facebookUser;
    private View rootview;
    private EmailUser emailUser;

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_user_profile, container, false);
        rootview.setClickable(true);
        rootview.findViewById(R.id.tvSignOut).setClickable(true);
        rootview.findViewById(R.id.tvSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: buttonClicked");
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
        (rootview.findViewById(R.id.iv_profile_pic)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Image Button Clicked");
                //implement code to change profile picture
            }
        });
        FirebaseDataAPI firebaseDataAPI = new FirebaseDataAPI();
        facebookUser = new FacebookUser();
        if (getArguments().getString("provider").equals("facebook")) {
            firebaseDataAPI.getData().getFBUsers(FirebaseAuth.getInstance().getCurrentUser().getUid()).enqueue(
                    new Callback<FacebookUser>() {
                        @Override
                        public void onResponse(Call<FacebookUser> call, Response<FacebookUser> response) {
                            if (response.isSuccessful()) {
                                facebookUser = response.body();
                                Picasso.with(UserProfile.this.context)
                                        .load(facebookUser.getPhotoURL())
                                        .fit()
                                        .placeholder(R.drawable.profile_circular_border_imageview)
                                        .error(R.drawable.ic_error)
                                        .into(((ImageView) rootview.findViewById(R.id.iv_profile_pic)));

                                CircleDrawable circleDrawable = new CircleDrawable
                                        (
                                                drawableToBitmap(
                                                        ((ImageView) rootview.findViewById(R.id.iv_profile_pic)
                                                        ).getDrawable()),
                                                true
                                        );
                                ((ImageView) rootview.findViewById(R.id.iv_profile_pic)).setImageBitmap
                                        (drawableToBitmap(circleDrawable));
                                ((TextView) rootview.findViewById(R.id.user_profile_name)).setText(facebookUser.getName());
                                ((TextView) rootview.findViewById(R.id.user_profile_short_bio)).setText(
                                        (facebookUser.getEmail() == null || facebookUser.getEmail().equals("")) ?
                                                facebookUser.getPhoneNumber() : facebookUser.getEmail());

                            } else {
                                Log.d(TAG, "onResponse: " + response.message());
                                Log.d(TAG, "onResponse: " + response.body());
                                Log.d(TAG, "onResponse: " + response.errorBody());
                                Log.d(TAG, "onResponse: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<FacebookUser> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getCause());
                            t.printStackTrace();
                            Log.d(TAG, "onFailure: " + t.getMessage());
                        }
                    });
        } else if (getArguments().getString("provider").equals("google")) {
            //TODO: after setting up google profile
        } else {
            if (getArguments().getString("provider").equals("email")) {
                firebaseDataAPI.getData().getEmailUsers(FirebaseAuth.getInstance().getCurrentUser().getUid()).enqueue(
                        new Callback<EmailUser>() {
                            @Override
                            public void onResponse(Call<EmailUser> call, Response<EmailUser> response) {
                                if (response.isSuccessful()) {
                                    emailUser = response.body();
                                    ((TextView) rootview.findViewById(R.id.user_profile_name)).setText(emailUser.getName());
                                    ((TextView) rootview.findViewById(R.id.user_profile_short_bio)).setText(
                                            emailUser.getEmail());

                                } else {
                                    Log.d(TAG, "onResponse: " + response.message());
                                    Log.d(TAG, "onResponse: " + response.body());
                                    Log.d(TAG, "onResponse: " + response.errorBody());
                                    Log.d(TAG, "onResponse: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<EmailUser> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + t.getCause());
                                t.printStackTrace();
                                Log.d(TAG, "onFailure: " + t.getMessage());
                            }
                        });
            }
        }
        return rootview;
    }
}
