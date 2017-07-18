package com.example.karan.traffikill.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.FacebookUser;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    protected FirebaseDatabase firebaseDatabase;
    CallbackManager callbackManager;
    LoginButton fbloginButton;
    TextView btnSignUp;

    @Override
    protected void onStart() {
        super.onStart();
        UserScreen.currentUser = UserScreen.userAuthentication.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserScreen.userAuthentication = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        fbloginButton = (LoginButton) findViewById(R.id.fb_login_button);
        btnSignUp = (TextView) findViewById(R.id.tv_signup);

        //Email or Phone Signup Method
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        //Facebook Login Method
        fbloginButton.setReadPermissions(Arrays.asList("user_profile", "public_profile", "email"));
        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUp);
            }
        });
    }

    private void saveFacebookLoginData(String facebook, String token) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        UserScreen.userAuthentication.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            UserScreen.currentUser = UserScreen.userAuthentication.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference ref = firebaseDatabase.getReference("https://crafty-router-159106.firebaseio.com/");
                            DatabaseReference usersRef = ref.child("users");
                            Map<String, FacebookUser> users = new HashMap<>();
                            users.put(UserScreen.currentUser.getUid(), new FacebookUser(UserScreen.currentUser.getDisplayName()));
                            if (usersRef.child(UserScreen.currentUser.getUid()) == null) {
                                usersRef.setValue(users);
                            } else {
                                Toast.makeText(LoginActivity.this, "You have already registered with TraffiKill",
                                        Toast.LENGTH_SHORT).show();
                            }
                            startActivity(new Intent(LoginActivity.this, UserScreen.class));
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
