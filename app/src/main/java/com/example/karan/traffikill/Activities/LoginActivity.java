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
import android.widget.TextView;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.truizlop.fabreveallayout.FABRevealLayout;
import com.truizlop.fabreveallayout.OnRevealChangeListener;

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private final int PERM_REQ_CODE = 123;
    CallbackManager callbackManager;
    ImageView fbloginButton, cancel;
    Button btnSignIn, btnGetStarted;
    FABRevealLayout btnSignUp;
    TextView tvForgotPassword;
    EditText etUsername, etPassword, etEmail, etSignUpUsername, etSignUpPassword, etConfirmPassword;
    ProgressBar progressBar;
    EmailAuthenticator emailAuthenticator;
    EditText etName;
    private boolean flag = true;

    public static boolean isValidEmail(CharSequence target) {
        return target != null && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_test);
        checkPermission(this, Manifest.permission.INTERNET);

        cancel = (ImageView) findViewById(R.id.cancel);
        cancel.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
        setupMainScreen(findViewById(R.id.content_login));
    }

    public void checkPermission(Context context, String perm) {
        //Don't need permission check in other activities as all fragments run on this activity's
        // stack itself so all permissions given apply to them as well
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
        tvForgotPassword = (TextView) mainView.findViewById(R.id.tvForgotPassword);
        progressBar.setVisibility(View.INVISIBLE);
        btnSignIn.setClickable(true);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (UserActivity.userAuthentication.getCurrentUser() != null) {
                    UserActivity.userAuthentication.signOut();
                    LoginManager.getInstance().logOut();
                    if (!login_checker())
                        return;
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
                    if (!login_checker())
                        return;
                    UserActivity.userAuthentication.signInWithEmailAndPassword(etUsername.getText().toString().trim(),
                            etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(LoginActivity.this, UserActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
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
                        Toast.makeText(LoginActivity.this, error.getCause().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUsername.getText().toString().equals("") || !isValidEmail(etUsername.getText().toString().trim())) {
                    Toast.makeText(LoginActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserActivity.userAuthentication.sendPasswordResetEmail(LoginActivity.this.etUsername.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Reset Email sent", Toast.LENGTH_SHORT).show();
                                } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    Toast.makeText(LoginActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private boolean login_checker() {
        if (etUsername.getText().toString().trim().equals("") || etPassword.getText().toString().trim().equals("")) {
            Toast.makeText(LoginActivity.this, "Email or Password can't be empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
        if (!isValidEmail(etUsername.getText().toString().trim())) {
            Toast.makeText(LoginActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
        return true;
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
                            Toast.makeText(LoginActivity.this, "Please Enter your Email", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
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
                        if (etSignUpPassword.getText().toString().length() < 6) {
                            Toast.makeText(LoginActivity.this, "Password Length should be greater than 6 characters",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!etSignUpPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                            Toast.makeText(LoginActivity.this, "Passwords Do Not Match!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (isValidUsername(etSignUpUsername.getText().toString().trim()) == false) {
//                            '/', '.', '#', '$', '[', or ']
                            Toast.makeText(LoginActivity.this, "Username can not contain any of the following: '/', '.' , '#' ," +
                                            " '$' , '[' , ']' , ' ' ",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (etSignUpUsername.getText().toString().equals("") || etSignUpPassword.getText().toString().equals("")) {
                            Toast.makeText(LoginActivity.this, "Username or Password can't be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseDatabase.getInstance().getReference().child("unauthorised").child("usernames").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d("Hello", "onDataChange: " + ((HashMap<String, String>) dataSnapshot.getValue()).size());
                                        if (((HashMap<Object, Object>) dataSnapshot.getValue()).containsKey(
                                                etSignUpUsername.getText().toString().trim())) {
                                            Toast.makeText(LoginActivity.this, "Username Already Exists", Toast.LENGTH_SHORT).show();
                                            flag = false;
                                            return;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(LoginActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT)
                                                .show();
                                        flag = false;
                                        return;
                                    }
                                });
                        if (!flag) {
                            btnGetStarted.setClickable(true);
                            return;
                        }
                        if (etSignUpPassword.getText().toString().equals(etConfirmPassword.getText().toString()) && flag) {
                            emailAuthenticator.initialise(LoginActivity.this,
                                    etName.getText().toString(),
                                    etEmail.getText().toString(),
                                    etSignUpPassword.getText().toString(),
                                    etSignUpUsername.getText().toString(),
                                    progressBar);
                            progressBar.setVisibility(View.VISIBLE);
                            emailAuthenticator.execute();
                            btnGetStarted.setClickable(false);
                        }
                    }
                });
            }
        });
    }

    private boolean isValidUsername(String trim) {
        return !(trim.contains("/") || trim.contains(".") ||
                trim.contains("#") || trim.contains("$") || trim.contains("[") || trim.contains("]") || trim.contains(" "));
    }

    private void prepareBackTransition(final FABRevealLayout fabRevealLayout) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fabRevealLayout.revealMainView();
            }
        }, 300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
