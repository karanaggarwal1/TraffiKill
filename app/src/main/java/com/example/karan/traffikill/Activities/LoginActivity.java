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
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.example.karan.traffikill.Services.GoogleAuthenticator;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.truizlop.fabreveallayout.FABRevealLayout;
import com.truizlop.fabreveallayout.OnRevealChangeListener;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 234;
    private final int PERM_REQ_CODE = 123;
    CallbackManager callbackManager;
    ImageView fbloginButton, cancel;
    Button btnSignIn, btnGetStarted;
    FABRevealLayout btnSignUp;
    EditText etUsername, etPassword, etEmail, etSignUpUsername, etSignUpPassword, etConfirmPassword;
    ProgressBar progressBar;
    EmailAuthenticator emailAuthenticator;
    EditText etName;
    private ImageView googleLoginButton;
    private GoogleApiClient mGoogleApiClient;

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            Log.d(TAG, "isValidEmail: " + Patterns.EMAIL_ADDRESS.matcher(target).matches());
            Log.d(TAG, "isValidEmail: " + target.toString());
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

        UserActivity.userAuthentication = FirebaseAuth.getInstance();
        if (UserActivity.currentUser != null) {
            UserActivity.userAuthentication.signOut();
            LoginManager.getInstance().logOut();
        }
        setupMainScreen((CardView) findViewById(R.id.content_login));
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

    public void setupMainScreen(View mainView) {
        etUsername = (EditText) mainView.findViewById(R.id.tvUsername);
        etPassword = (EditText) mainView.findViewById(R.id.tvPassword);
        progressBar = (ProgressBar) mainView.findViewById(R.id.determinateBar);
        btnSignIn = (Button) mainView.findViewById(R.id.btnSignIn);
        fbloginButton = (ImageView) mainView.findViewById(R.id.fbLoginButton);
        googleLoginButton = (ImageView) mainView.findViewById(R.id.btnGoogleSignIn);
        Log.d(TAG, "onMainViewAppeared: " + mainView.getId());
        progressBar.setVisibility(View.INVISIBLE);
        btnSignIn.setClickable(true);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ButtonClicked");
                progressBar.setVisibility(View.VISIBLE);
                if (UserActivity.userAuthentication.getCurrentUser() != null) {
                    UserActivity.userAuthentication.signOut();
                    LoginManager.getInstance().logOut();
                    UserActivity.userAuthentication.signInWithEmailAndPassword(etUsername.getText().toString().trim(),
                            etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(LoginActivity.this, UserActivity.class));
                            }
                        }
                    });
                } else {
                    UserActivity.userAuthentication.signInWithEmailAndPassword(etUsername.getText().toString(),
                            etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(LoginActivity.this, UserActivity.class));
                            }
                        }
                    });
                    if (UserActivity.userAuthentication.getCurrentUser() != null)
                        startActivity(new Intent(LoginActivity.this, UserActivity.class));
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
                        facebookAuthenticator.initializor(LoginActivity.this, progressBar, UserActivity.userAuthentication,
                                loginResult.getAccessToken().getUserId());
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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.google_sign_in_id))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserActivity.currentUser = UserActivity.userAuthentication.getCurrentUser();
    }

    private void configureFABReveal(FABRevealLayout fabRevealLayout) {
        fabRevealLayout.setOnRevealChangeListener(new OnRevealChangeListener() {
            @Override
            public void onMainViewAppeared(FABRevealLayout fabRevealLayout, View mainView) {
                cancel.setVisibility(View.GONE);
                setupMainScreen(mainView);
            }

            @Override
            public void onSecondaryViewAppeared(final FABRevealLayout fabRevealLayout, View secondaryView) {
                cancel.setVisibility(View.VISIBLE);
                etName = (EditText) secondaryView.findViewById(R.id.etName);
                etEmail = (EditText) secondaryView.findViewById(R.id.etEmail);
                etSignUpPassword = (EditText) secondaryView.findViewById(R.id.etPassword);
                etConfirmPassword = (EditText) secondaryView.findViewById(R.id.etConfirmPassWord);
                etSignUpUsername = (EditText) secondaryView.findViewById(R.id.etUsername);
                btnGetStarted = (Button) secondaryView.findViewById(R.id.btnGetStarted);
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                etName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().trim().equals("")) {
                            btnGetStarted.setClickable(false);
                            Toast.makeText(LoginActivity.this, "Please Enter your name", Toast.LENGTH_SHORT).show();
                        } else {
                            btnGetStarted.setClickable(true);
                        }
                    }
                });
                etEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().trim().equals("")) {
                            btnGetStarted.setClickable(false);
                            Toast.makeText(LoginActivity.this, "Please Enter your name", Toast.LENGTH_SHORT).show();
                        } else {
                            btnGetStarted.setClickable(true);
                        }
                    }
                });
                etSignUpPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().trim().equals("")) {
                            btnGetStarted.setClickable(false);
                            Toast.makeText(LoginActivity.this, "Please Enter your name", Toast.LENGTH_SHORT).show();
                        } else {
                            btnGetStarted.setClickable(true);
                        }
                    }
                });
                if (!etName.getText().toString().equals("") &&
                        !etEmail.getText().toString().equals("") &&
                        !etSignUpPassword.getText().toString().equals("")) {
                    btnGetStarted.setClickable(true);
                }
                progressBar.setVisibility(View.INVISIBLE);
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
                                    etName.getText().toString(),
                                    etEmail.getText().toString(),
                                    etSignUpPassword.getText().toString(),
                                    etSignUpUsername.getText().toString());
                            progressBar.setVisibility(View.VISIBLE);
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
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
                googleAuthenticator.initializor(LoginActivity.this, LoginActivity.this.progressBar);
                googleAuthenticator.execute(account);
            } else {

            }
        }
    }


}
