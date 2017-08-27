package com.example.karan.traffikill.Services;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.karan.traffikill.Activities.UserActivity;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FacebookAuthenticator extends AsyncTask<AccessToken, Integer, Boolean> {

    private Context context;
    private ProgressBar progressBar;
    private boolean retval;
    private FirebaseAuth firebaseAuth;
    private String userID;

    public void initializor(Context context, ProgressBar progressBar, FirebaseAuth firebaseAuth, String userID) {
        this.context = context;
        this.progressBar = progressBar;
        this.firebaseAuth = firebaseAuth;
        this.userID = userID;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        progressBar.setProgress(100);
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected Boolean doInBackground(AccessToken... params) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(params[0].getToken());
        this.firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FacebookAuthenticator.this.context, "Login Successful", Toast.LENGTH_SHORT).show();
                            publishProgress(50);
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            final DatabaseReference ref = firebaseDatabase.getReference().child("authorised");
                            final DatabaseReference usersRef = ref.child("usersFB");
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
                                            userDetails.put("userID", FacebookAuthenticator.this.userID);
                                            userDetails.put("pictureSet", "false");
                                            usersRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(userDetails);
                                            retval = true;
                                            publishProgress(100);
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(FacebookAuthenticator.this.context, "You have already registered with TraffiKill",
                                                    Toast.LENGTH_SHORT).show();
                                            retval = true;
                                            publishProgress(100);
                                        }
                                    });
                            FacebookAuthenticator.this.context.startActivity(new Intent(FacebookAuthenticator.this.context,
                                    UserActivity.class));
                        } else {
                            Toast.makeText(FacebookAuthenticator.this.context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            retval = false;
                            publishProgress(100);
                        }
                    }
                });
        return retval;
    }
}
