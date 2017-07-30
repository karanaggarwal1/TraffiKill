package com.example.karan.traffikill.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karan.traffikill.Activities.LoginActivity;
import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.EmailUser;
import com.example.karan.traffikill.models.FacebookUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Karan on 28-07-2017.
 */

public class UserProfile extends Fragment {
    public static final String TAG = "FragmentUserProfile";
    public Context context;
    private FacebookUser facebookUser;
    private View rootview;
    private EmailUser emailUser;

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
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });
        (rootview.findViewById(R.id.user_profile_photo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Image Button Clicked");
                //implement code to change profile picture
            }
        });
        facebookUser = new FacebookUser();
        if (getArguments().getString("provider").equals("facebook")) {
            rootview.findViewById(R.id.changePassword).setVisibility(View.GONE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                    child("authorised").
                    child("usersFB").
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    facebookUser = dataSnapshot.getValue(FacebookUser.class);
                    String photoUrl = "http://graph.facebook.com/" + facebookUser.getUserID() +
                            "/picture?width=100&height=100";
                    if (getContext() != null) {
                        Picasso.Builder builder = new Picasso.Builder(getContext());
                        builder.listener(new Picasso.Listener() {
                            @Override
                            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    ((ImageView) (rootview.findViewById(R.id.user_profile_photo))).
                                            setImageDrawable(getContext().getDrawable(R.drawable.ic_error));
                                }
                                Log.d(TAG, "onImageLoadFailed: " + exception.getCause());
                            }
                        });
                        builder.downloader(new OkHttpDownloader(getContext()));
                        builder.build().load(photoUrl).into((ImageView) (rootview.findViewById(R.id.user_profile_photo)));
                    }
                    ((TextView) rootview.findViewById(R.id.user_profile_name)).setText(facebookUser.getName());
                    ((TextView) rootview.findViewById(R.id.user_profile_short_bio)).setText(
                            (facebookUser.getEmail() == null || facebookUser.getEmail().equals("")) ?
                                    facebookUser.getPhoneNumber() : facebookUser.getEmail());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else if (getArguments().getString("provider").equals("google")) {
            //TODO: after setting up google profile
        } else {
            if (getArguments().getString("provider").equals("email")) {
                rootview.findViewById(R.id.changePassword).setVisibility(View.VISIBLE);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                        child("authorised").
                        child("usersEmail").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            }
        }
        rootview.findViewById(R.id.changePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changePasswordClicked");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Resetting the Password will Log you Out")
                        .setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                rootview.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                if (getArguments().getString("provider").equals("email"))
                                    FirebaseAuth.getInstance().sendPasswordResetEmail(
                                            FirebaseAuth.getInstance().getCurrentUser().getEmail()).
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getActivity(),
                                                            "Reset Password Email Sent",
                                                            Toast.LENGTH_SHORT).
                                                            show();
                                                    FirebaseAuth.getInstance().signOut();
                                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    getActivity().finish();
                                                    rootview.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                                                }
                                            });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                return;
                            }
                        });

                builder.show();
            }
        });
        return rootview;
    }

}
