package com.example.karan.traffikill.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.karan.traffikill.Activities.UserScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Karan on 27-07-2017.
 */

public class EmailAuthenticator extends AsyncTask<Void, Integer, Boolean> {
    private Context context;
    private FirebaseAuth firebaseAuth;
    private String etEmail, etPassword;
    private boolean retval = false;
    private String userName;

    @Override
    protected Boolean doInBackground(Void... params) {

        firebaseAuth.createUserWithEmailAndPassword(etEmail,
                etPassword).addOnCompleteListener((Activity) this.context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("EmailAuthenticator", "signInWithCredential:success");
                    Toast.makeText(EmailAuthenticator.this.context, "Login Successful", Toast.LENGTH_SHORT).show();
                    publishProgress(50);
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference ref = firebaseDatabase.getReference();
                    DatabaseReference usersRef = ref.child("usersEmail");
                    DatabaseReference currentUserReference = ref.child(EmailAuthenticator.this.userName);
                    Map<String, String> userDetails = new HashMap<>();
                    userDetails.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    userDetails.put("email", EmailAuthenticator.this.etEmail);
                    currentUserReference.setValue(userDetails);
                    retval = true;
                    publishProgress(100);
                    EmailAuthenticator.this.context.startActivity(new Intent(EmailAuthenticator.this.context, UserScreen.class));
                }else{
                    Log.d("EmailAuthenticator", "onComplete: "+task.getException());
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

    public void initialise(Context context, String etEmail, String etPassword, String userName) {
        this.context = context;
        this.etEmail = etEmail;
        this.etPassword = etPassword;
        this.userName=userName;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }
}
