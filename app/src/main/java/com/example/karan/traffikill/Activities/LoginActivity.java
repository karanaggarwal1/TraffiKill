package com.example.karan.traffikill.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karan.traffikill.R;
import com.example.karan.traffikill.Services.FacebookAuthenticator;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

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
    ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();
        UserScreen.currentUser = UserScreen.userAuthentication.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO: add a google+ sign up method
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        checkPermission(this, Manifest.permission.INTERNET);

        etUsername = (EditText) findViewById(R.id.tvUsername);
        etPassword = (EditText) findViewById(R.id.tvPassword);
        progressBar = (ProgressBar) findViewById(R.id.determinateBar);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        btnSignIn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

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

        fbloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "user_friends, email"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        FacebookAuthenticator facebookAuthenticator = new FacebookAuthenticator();
                        btnSignIn.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        facebookAuthenticator.initializor(LoginActivity.this,progressBar);
                        facebookAuthenticator.execute(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Operation was Cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "onError: " + error.getCause());
                    }
                });
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
                        if (UserScreen.userAuthentication.getCurrentUser() != null)
                            startActivity(new Intent(LoginActivity.this, UserScreen.class));
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

}
