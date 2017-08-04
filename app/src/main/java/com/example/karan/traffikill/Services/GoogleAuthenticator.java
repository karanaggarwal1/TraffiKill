package com.example.karan.traffikill.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.karan.traffikill.Activities.UserActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Karan on 05-08-2017.
 */

public class GoogleAuthenticator extends AsyncTask<GoogleSignInAccount, Void, Void> {
    private static final String TAG = "GoogleAuthenticator";
    private Context context;
    private ProgressBar progressBar;

    public void initializor(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    @Override
    protected Void doInBackground(GoogleSignInAccount... params) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + params[0].getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(params[0].getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener((Activity) GoogleAuthenticator.this.context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            final DatabaseReference ref = firebaseDatabase.getReference().child("authorised");
                            final DatabaseReference usersRef = ref.child("usersGoogle");
                            usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent
                                    (new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Map<String, Object> userDetails = new HashMap<>();
                                            userDetails.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            if (FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
                                                userDetails.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                            }
                                            if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null) {
                                                userDetails.put("phoneNumber", FirebaseAuth.getInstance().getCurrentUser().
                                                        getPhoneNumber());
                                            }
                                            if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
                                                userDetails.put("photoURL", FirebaseAuth.getInstance().getCurrentUser().
                                                        getPhotoUrl().toString());
                                            }
                                            if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {
                                                userDetails.put("name", FirebaseAuth.getInstance().getCurrentUser().
                                                        getDisplayName().toString());
                                            }
                                            usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userDetails);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(GoogleAuthenticator.this.context,
                                                    "You have already registered with TraffiKill",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            GoogleAuthenticator.this.progressBar.setVisibility(View.INVISIBLE);
                            GoogleAuthenticator.this.context.startActivity(new Intent(GoogleAuthenticator.this.context,
                                    UserActivity.class));
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(GoogleAuthenticator.this.context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            GoogleAuthenticator.this.progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        return null;
    }
}
