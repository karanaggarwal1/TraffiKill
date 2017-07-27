package com.example.karan.traffikill.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.karan.traffikill.R;
import com.example.karan.traffikill.Services.EmailAuthenticator;
import com.example.karan.traffikill.Services.FacebookAuthenticator;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.truizlop.fabreveallayout.FABRevealLayout;
import com.truizlop.fabreveallayout.OnRevealChangeListener;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private final int PERM_REQ_CODE = 123;
    CallbackManager callbackManager;
    ImageView fbloginButton, cancel;
    Button btnSignIn, btnGetStarted;
    FABRevealLayout btnSignUp;
    EditText etUsername, etPassword, etEmail, etSignUpUsername, etSignUpPassword, etConfirmPassword;
    ProgressBar progressBar;
    EmailAuthenticator emailAuthenticator;

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_test);

        checkPermission(this, Manifest.permission.INTERNET);

        cancel = (ImageView) findViewById(R.id.cancel);

        btnSignUp = (FABRevealLayout) findViewById(R.id.fab_reveal_layout);
        configureFABReveal(btnSignUp);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareBackTransition(btnSignUp);
            }
        });

        UserScreen.userAuthentication = FirebaseAuth.getInstance();
        if (UserScreen.currentUser != null) {
            UserScreen.userAuthentication.signOut();
        }
        if (UserScreen.userAuthentication.getCurrentUser() != null) {
            startActivity(new Intent(this, UserScreen.class));
        }

        etUsername = (EditText) findViewById(R.id.tvUsername);
        etPassword = (EditText) findViewById(R.id.tvPassword);
        progressBar = (ProgressBar) findViewById(R.id.determinateBar);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        fbloginButton = (ImageView) findViewById(R.id.fbLoginButton);

        progressBar.setVisibility(View.INVISIBLE);
        btnSignIn.setClickable(true);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ButtonClicked");
                if (UserScreen.userAuthentication.getCurrentUser() != null) {
                    UserScreen.userAuthentication.signOut();
                    UserScreen.userAuthentication.signInWithEmailAndPassword(etUsername.getText().toString().trim(),
                            etPassword.getText().toString());
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                    UserScreen.userAuthentication.signInWithEmailAndPassword(etUsername.getText().toString(),
                            etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setProgress(100);
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(LoginActivity.this, UserScreen.class));
                            } else {
                                Log.d(TAG, "onComplete: " + task.getException());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                progressBar.setProgress(100);
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                progressBar.setProgress(100);
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "onFailure: " + e.getCause());
                            }
                        }
                    });
                }
            }
        });
        fbloginButton.setClickable(true);
        fbloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Button Clicked");
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "user_friends, email"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        FacebookAuthenticator facebookAuthenticator = new FacebookAuthenticator();
                        progressBar.setVisibility(View.VISIBLE);
                        facebookAuthenticator.initializor(LoginActivity.this, progressBar, UserScreen.userAuthentication);
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

    @Override
    protected void onStart() {
        super.onStart();
        UserScreen.currentUser = UserScreen.userAuthentication.getCurrentUser();
    }

    private void configureFABReveal(FABRevealLayout fabRevealLayout) {
        fabRevealLayout.setOnRevealChangeListener(new OnRevealChangeListener() {
            @Override
            public void onMainViewAppeared(FABRevealLayout fabRevealLayout, View mainView) {
                cancel.setVisibility(View.GONE);

                etUsername = (EditText) mainView.findViewById(R.id.tvUsername);
                etPassword = (EditText) mainView.findViewById(R.id.tvPassword);
                progressBar = (ProgressBar) mainView.findViewById(R.id.determinateBar);
                btnSignIn = (Button) mainView.findViewById(R.id.btnSignIn);
                fbloginButton = (ImageView) mainView.findViewById(R.id.fbLoginButton);
                Log.d(TAG, "onMainViewAppeared: " + mainView.getId());
                progressBar.setVisibility(View.INVISIBLE);
                btnSignIn.setClickable(true);
                btnSignIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: ButtonClicked");
                        if (UserScreen.userAuthentication.getCurrentUser() != null) {
                            UserScreen.userAuthentication.signOut();
                            LoginManager.getInstance().logOut();
                            UserScreen.userAuthentication.signInWithEmailAndPassword(etUsername.getText().toString().trim(),
                                    etPassword.getText().toString());
                        } else {
                            UserScreen.userAuthentication.signInWithEmailAndPassword(etUsername.getText().toString(),
                                    etPassword.getText().toString());
                            if (UserScreen.userAuthentication.getCurrentUser() != null)
                                startActivity(new Intent(LoginActivity.this, UserScreen.class));
                        }
                    }
                });
                fbloginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Button Clicked");
                        LoginManager.getInstance().logOut();
                        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                                Arrays.asList("public_profile", "user_friends, email"));
                        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                FacebookAuthenticator facebookAuthenticator = new FacebookAuthenticator();
                                progressBar.setVisibility(View.VISIBLE);
                                facebookAuthenticator.initializor(LoginActivity.this, progressBar, UserScreen.userAuthentication);
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
            }

            @Override
            public void onSecondaryViewAppeared(final FABRevealLayout fabRevealLayout, View secondaryView) {
                cancel.setVisibility(View.VISIBLE);
                etEmail = (EditText) secondaryView.findViewById(R.id.etEmail);
                etSignUpPassword = (EditText) secondaryView.findViewById(R.id.etPassword);
                etConfirmPassword = (EditText) secondaryView.findViewById(R.id.etConfirmPassWord);
                etSignUpUsername = (EditText) secondaryView.findViewById(R.id.etUsername);
                btnGetStarted = (Button) secondaryView.findViewById(R.id.btnGetStarted);
                etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                emailAuthenticator = new EmailAuthenticator();
                btnGetStarted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isValidEmail(etEmail.getText().toString().trim())) {
                            Toast.makeText(LoginActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!etSignUpPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                            Toast.makeText(LoginActivity.this, "Passwords Do Not Match!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (etSignUpPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                            emailAuthenticator.initialise(LoginActivity.this,
                                    etEmail.getText().toString(),
                                    etSignUpPassword.getText().toString(),
                                    etSignUpUsername.getText().toString());
                            emailAuthenticator.execute();
                            btnGetStarted.setClickable(false);
                        }
                    }
                });

            }
        });
    }

    private void prepareBackTransition(final FABRevealLayout fabRevealLayout) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fabRevealLayout.revealMainView();
            }
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
