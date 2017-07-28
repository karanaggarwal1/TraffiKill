package com.example.karan.traffikill.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.karan.traffikill.Activities.LoginActivity;
import com.example.karan.traffikill.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Karan on 28-07-2017.
 */

public class UserProfile extends Fragment {
    public static final String TAG = "FragmentUserProfile";
    public Context context;

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview;
        rootview = inflater.inflate(R.layout.fragment_user_profile, container, false);
        rootview.setClickable(true);
        rootview.findViewById(R.id.tvSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: buttonClicked");
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent((getContext()), LoginActivity.class));
            }
        });
        return rootview;
    }
}
