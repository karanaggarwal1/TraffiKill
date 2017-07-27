package com.example.karan.traffikill.Services;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.karan.traffikill.Activities.UserScreen;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Karan on 27-07-2017.
 */

public class FacebookAuthenticator extends AsyncTask<AccessToken, Integer, Boolean> {

    private String TAG = "FacebookAuthenticator";
    private Context context;
    private ProgressBar progressBar;
    private boolean retval;
    private FirebaseAuth firebaseAuth;
    public void initializor(Context context, ProgressBar progressBar, FirebaseAuth firebaseAuth) {
        this.context = context;
        this.progressBar = progressBar;
        this.firebaseAuth=firebaseAuth;
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
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(FacebookAuthenticator.this.context, "Login Successful", Toast.LENGTH_SHORT).show();
                            publishProgress(50);
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference ref = firebaseDatabase.getReference();
                            DatabaseReference usersRef = ref.child("usersFB");
                            if (usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()) == null) {
                                DatabaseReference currentUserReference = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Map<String, String> userDetails = new HashMap<>();
                                userDetails.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                if (FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
                                    userDetails.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                }
                                if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null) {
                                    userDetails.put("phoneNumber", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                                }
                                if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
                                    userDetails.put("photoURL", FirebaseAuth.getInstance().getCurrentUser().toString());
                                }
                                currentUserReference.setValue(userDetails);
                                retval = true;
                                publishProgress(100);
                            } else {
                                Toast.makeText(FacebookAuthenticator.this.context, "You have already registered with TraffiKill",
                                        Toast.LENGTH_SHORT).show();
                                retval = true;
                                publishProgress(100);
                            }
                            FacebookAuthenticator.this.context.startActivity(new Intent(FacebookAuthenticator.this.context, UserScreen.class));
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
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
