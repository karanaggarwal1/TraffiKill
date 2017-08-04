package com.example.karan.traffikill.Fragments;

import android.app.Activity;
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

import com.example.karan.traffikill.Activities.CustomLocation;
import com.example.karan.traffikill.Activities.LoginActivity;
import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.FacebookUser;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

/**
 * Created by Karan on 28-07-2017.
 */

public class UserProfile extends Fragment {
    public static final String TAG = "FragmentUserProfile";
    private static final int PICK_IMAGE_REQUEST = 123;
    public Context context;
    String provider;
    private FacebookUser facebookUser;
    private View rootview;
    private String path;
    private StorageReference storageReference;
    private boolean pictureSet;
    private File localFile = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            storageReference.child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg").
                    putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        pictureSet = true;
                        Toast.makeText(UserProfile.this.context, "File Uploaded", Toast.LENGTH_SHORT).show();
                        ifPictureSet();
                        setProfilePicture();
                    }
                }
            });
        }
        if (requestCode == 234) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(UserProfile.this.getContext(), "Place Selected", Toast.LENGTH_LONG).show();
                Intent outgoingIntent = new Intent(UserProfile.this.getContext(), CustomLocation.class);
                outgoingIntent.putExtra("latitude", PlacePicker.getPlace(UserProfile.this.getContext(), data).getLatLng().latitude);
                outgoingIntent.putExtra("longitude", PlacePicker.getPlace(UserProfile.this.getContext(), data).getLatLng().longitude);
                startActivity(outgoingIntent);
            }
        }
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean ifPictureSet() {
        FirebaseDatabase.getInstance().getReference().child("authorised").child("usersEmail").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pictureSet").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            pictureSet = Boolean.parseBoolean((String) dataSnapshot.getValue());
                            FirebaseDatabase.getInstance().getReference().child("authorised").child("usersEmail").
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pictureSet").
                                    setValue("true");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: " + databaseError.getDetails());
                    }
                });
        return pictureSet;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        storageReference = FirebaseStorage.getInstance().getReference();
        rootview = inflater.inflate(R.layout.fragment_user_profile, container, false);
        rootview.setClickable(true);
        rootview.findViewById(R.id.changeLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(intentBuilder.build((Activity) UserProfile.this.getContext()), 234);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d(TAG, "onClick: " + e.getCause() + "\n" + e.getMessage());
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d(TAG, "onClick: " + e.getCause() + "\n" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
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
                    Log.d(TAG, "onCancelled: " + databaseError.getDetails() + "\n" + databaseError.getMessage());
                }
            });

        } else if (getArguments().getString("provider").equals("google")) {
            rootview.findViewById(R.id.changePassword).setVisibility(View.GONE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                    child("authorised").
                    child("usersGoogle").
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("name") != null && dataSnapshot.child("name").getValue() != null) {
                        ((TextView) rootview.findViewById(R.id.user_profile_name)).setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if (dataSnapshot.child("photoURL") != null && dataSnapshot.child("photoURL").getValue() != null) {
                        Picasso.with(UserProfile.this.getContext())
                                .load(Uri.parse(dataSnapshot.child("photoURL").getValue().toString()))
                                .fit()
                                .placeholder(R.drawable.ic_placeholder)
                                .error(R.drawable.ic_error)
                                .into((ImageView) rootview.findViewById(R.id.user_profile_photo));
                    }
                    ((TextView) rootview.findViewById(R.id.user_profile_short_bio)).setText(dataSnapshot.child("email").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError.getDetails() + "\n" + databaseError.getMessage());
                }
            });
        } else {
            if (getArguments().getString("provider").equals("email")) {
                rootview.findViewById(R.id.changePassword).setVisibility(View.VISIBLE);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                        child("authorised").
                        child("usersEmail").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ((TextView) (rootview.findViewById(R.id.user_profile_name))).setText
                                (dataSnapshot.child("name").getValue().toString());
                        ((TextView) (rootview.findViewById(R.id.user_profile_short_bio))).setText
                                (dataSnapshot.child("email").getValue().toString() +
                                        " \n@" + dataSnapshot.child("username").getValue().toString());
                        pictureSet = Boolean.parseBoolean(dataSnapshot.child("pictureSet").getValue().toString());
//                        ((ImageView)(rootview.findViewById(R.id.user_profile_photo)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                        Log.d(TAG, "onCancelled: " + databaseError.getDetails());
                        Log.d(TAG, "onCancelled: " + databaseError.toException().getCause());
                        Log.d(TAG, "onCancelled: " + databaseError.toException().getMessage());
                    }
                });

            }
            (rootview.findViewById(R.id.user_profile_photo)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Image Button Clicked");
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
            });
            if (pictureSet) {
                setProfilePicture();
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

    private void setProfilePicture() {
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        storageReference = storageReference.child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
        storageReference.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        if (localFile != null) {
                            ((ImageView) (rootview.findViewById(R.id.user_profile_photo))).setImageURI
                                    (Uri.fromFile(localFile));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(UserProfile.this.getContext(), "Error getting file", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
