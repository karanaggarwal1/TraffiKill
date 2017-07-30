package com.example.karan.traffikill.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.karan.traffikill.Activities.UserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.karan.traffikill.Fragments.UserProfile.TAG;

/**
 * Created by Karan on 27-07-2017.
 */

public class EmailAuthenticator extends AsyncTask<Void, Integer, Boolean> {
    private Context context;
    private FirebaseAuth firebaseAuth;
    private String etEmail, etPassword, etName;
    private boolean retval = false;
    private String userName;

    @Override
    protected Boolean doInBackground(Void... params) {

        firebaseAuth.createUserWithEmailAndPassword(etEmail,
                etPassword).addOnCompleteListener((Activity) this.context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseAuth.signInWithEmailAndPassword(etEmail, etPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (FirebaseAuth.getInstance().getCurrentUser() == null)
                                    Log.d(TAG, "onComplete: null");
                                Log.d("EmailAuthenticator", "signInWithCredential:success");
                                Toast.makeText(EmailAuthenticator.this.context, "Login Successful", Toast.LENGTH_SHORT).show();
                                publishProgress(50);
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference ref = firebaseDatabase.getReference().child("authorised");
                                DatabaseReference userNameReference = firebaseDatabase.getReference().
                                        child("unauthorised").child("usernames");
                                DatabaseReference usersRef = ref.child("usersEmail");
                                Map<String, String> unauthorisedData = new HashMap<>();
                                unauthorisedData.put(EmailAuthenticator.this.userName, EmailAuthenticator.this.etEmail);
                                userNameReference.setValue(unauthorisedData);
                                DatabaseReference currentUserReference = usersRef.child(FirebaseAuth.getInstance().
                                        getCurrentUser().getUid());
                                Log.d(TAG, "onComplete: " + FirebaseAuth.getInstance().
                                        getCurrentUser().getUid());
                                Map<String, String> userDetails = new HashMap<>();
                                userDetails.put("name", EmailAuthenticator.this.etName);
                                userDetails.put("username", EmailAuthenticator.this.userName);
                                userDetails.put("email", EmailAuthenticator.this.etEmail);
                                userDetails.put("verified", "false");

                                currentUserReference.setValue(userDetails).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.getCause());
                                        Log.d(TAG, "onFailure: " + e.getStackTrace());
                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                                        Log.d(TAG, "onFailure: " + e.getClass());
                                    }
                                });
                                retval = true;
                                publishProgress(90);
                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EmailAuthenticator.this.context,
                                                    "Verification Email Sent",
                                                    Toast.LENGTH_SHORT).show();
                                            publishProgress(100);
                                        }
                                    }
                                });
                            }
                        }
                    });

                    EmailAuthenticator.this.context.startActivity(new Intent(EmailAuthenticator.this.context, UserActivity.class));
                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(EmailAuthenticator.this.context, "User already exists", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("EmailAuthenticator", "onComplete: " + task.getException());
                }
            }

        });
        return retval;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    public void initialise(Context context, String etName, String etEmail, String etPassword, String userName) {
        this.context = context;
        this.etEmail = etEmail;
        this.etPassword = etPassword;
        this.userName = userName;
        this.etName = etName;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }
}
