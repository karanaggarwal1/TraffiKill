package com.example.karan.traffikill.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.FacebookUser;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static boolean flag = true, flagPhone = false;
    private final int PERM_REQ_CODE = 123;
    protected FirebaseDatabase firebaseDatabase;
    CallbackManager callbackManager;
    LoginButton fbloginButton;
    TextView btnSignUp, tvSignInWithPhoneNumber;
    Button btnSignIn;
    EditText etUsername, etPassword;

    @Override
    protected void onStart() {
        super.onStart();
        UserScreen.currentUser = UserScreen.userAuthentication.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO: add a google+ sign up method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkPermission(this, Manifest.permission.INTERNET);

        etUsername = (EditText) findViewById(R.id.tvUsername);
        etPassword = (EditText) findViewById(R.id.tvPassword);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        tvSignInWithPhoneNumber = (TextView) findViewById(R.id.tvsignInWithPhoneNumber);

        if (getIntent() != null) {
            if (getIntent().getStringExtra("email") != null) {
                etUsername.setText(getIntent().getStringExtra("email"));
            }
            if (getIntent().getStringExtra("password") != null) {
                etPassword.setText(getIntent().getStringExtra("password"));
            }
        }

        UserScreen.userAuthentication = FirebaseAuth.getInstance();
        if (UserScreen.currentUser != null) {
            UserScreen.userAuthentication.signOut();
        }
        if (UserScreen.userAuthentication.getCurrentUser() != null) {
            startActivity(new Intent(this, UserScreen.class));
        }
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
        fbloginButton.setReadPermissions(Arrays.asList("user_friends", "public_profile", "email"));
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
                signUp.putExtra("type", "email");
                startActivity(signUp);
            }
        });
        //Email login method
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    if (UserScreen.userAuthentication.getCurrentUser() != null) {
                        UserScreen.userAuthentication.signOut();
                        UserScreen.userAuthentication.signInWithEmailAndPassword(etUsername.getText().toString(),
                                etPassword.getText().toString());
                    } else {
                        UserScreen.userAuthentication.signInWithEmailAndPassword(etUsername.getText().toString(),
                                etPassword.getText().toString());
                    }
                } else {
                    if (UserScreen.userAuthentication.getCurrentUser() != null) {
                        UserScreen.userAuthentication.signOut();
                        Intent launchPhoneSignIn = new Intent(LoginActivity.this, SignUpActivity.class);
                        launchPhoneSignIn.putExtra("type", "phone");
                        startActivity(launchPhoneSignIn);
                    } else {

                    }
                }
            }
        });
        tvSignInWithPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    flag = false;
                    changeUIElements("normal");
                } else {
                    flag = true;
                    changeUIElements("phone");
                }
            }
        });
    }

    public void checkPermission(Context context, String perm) {
        //TODO: Implement a permission driven interface in other activities as well
        if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{perm}, PERM_REQ_CODE);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, perm)) {
            Toast.makeText(context, "Give the permission please.", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeUIElements(String type) {
        if (type.equals("normal")) {
            tvSignInWithPhoneNumber.setText("Sign in with Phone Number Instead");
            etUsername.setInputType(InputType.TYPE_CLASS_TEXT);
            etPassword.setVisibility(View.VISIBLE);
            etUsername.setHint("Username");
        } else {
            tvSignInWithPhoneNumber.setText("Sign in with Email Address Instead");
            etUsername.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
            etPassword.setVisibility(View.INVISIBLE);
            etUsername.setHint("Phone Number");
        }
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
                            Log.d(TAG, "onComplete: " + UserScreen.currentUser);
                            Log.d(TAG, "onComplete: " + UserScreen.currentUser.getDisplayName());
                            Log.d(TAG, "onComplete: " + UserScreen.currentUser.getEmail());
                            Log.d(TAG, "onComplete: " + UserScreen.currentUser.getUid());
                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference ref = firebaseDatabase.getReference();
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
