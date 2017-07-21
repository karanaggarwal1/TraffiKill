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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karan.traffikill.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerificationEmail extends AppCompatActivity {
    private final int PERM_REQ_CODE = 123;
    Intent incomingIntent;
    EditText etEmail, etName, etPassword, etConfirmPassword;
    TextView tvSamePasswords;
    Button btnSubmit;
    private FirebaseAuth userAuthentication;
    private FirebaseUser currentUser;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_email);

        checkPermission(this, Manifest.permission.INTERNET);

        userAuthentication = FirebaseAuth.getInstance();
        incomingIntent = getIntent();
        if (incomingIntent.getStringExtra("type").equals("email")) {
            findViewsbyId();
            initialiseState();
            etEmail.setText(incomingIntent.getStringExtra("email"));
            etPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().length() < 8) {
                        EnableViewProperties(tvSamePasswords);
                        tvSamePasswords.setText("Password Length can not be \n less than 8 characters");
                    }
                    if (s.toString().matches("^([a-zA-Z+]+[0-9+]+)$") == false) {
                        EnableViewProperties(tvSamePasswords);
                        tvSamePasswords.setText("Password must have atleast one number");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() >= 8 &&
                            s.toString().matches("^([a-zA-Z+]+[0-9+]+)$")) {
                        HideViewProperties(tvSamePasswords);
                        etConfirmPassword.setEnabled(true);
                    }
                    if (s.toString().equals(etPassword.getText().toString())) {
                        HideViewProperties(tvSamePasswords);
                    }
                    if (!tvSamePasswords.isEnabled()) {
                        btnSubmit.setEnabled(true);
                    }
                }
            });
            etConfirmPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString() != etPassword.getText().toString()) {
                        EnableViewProperties(tvSamePasswords);
                        tvSamePasswords.setText("Passwords do not match");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().equals(etPassword.getText().toString())) {
                        HideViewProperties(tvSamePasswords);
                    }
                    if (!tvSamePasswords.isEnabled()) {
                        btnSubmit.setEnabled(true);
                    }
                }
            });

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userAuthentication.createUserWithEmailAndPassword(etEmail.getText().toString(),
                            etPassword.getText().toString()).addOnCompleteListener(VerificationEmail.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUser = userAuthentication.getCurrentUser();
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = firebaseDatabase.getReference();
                                databaseReference.child("users").setValue(currentUser.getUid(), currentUser.getEmail());
                                databaseReference.child("users").child(currentUser.getUid()).
                                        setValue("provider", "email");
                                databaseReference.child("users").child(currentUser.getUid()).
                                        setValue("name", etName.getText().toString());
                                currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(VerificationEmail.this,
                                                    "Verification Email sent to " + currentUser.getEmail(),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(VerificationEmail.this,
                                                    "Failed to send verification email.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                Intent outgoingIntent = new Intent(VerificationEmail.this, LoginActivity.class);
                                outgoingIntent.putExtra("email", currentUser.getEmail());
                                outgoingIntent.putExtra("name", etName.getText().toString());
                                outgoingIntent.putExtra("password", etPassword.getText().toString());
                                startActivity(outgoingIntent);
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(VerificationEmail.this, "Email Address Exists", Toast.LENGTH_LONG).show();
                                    return;
                                } else {
                                    Toast.makeText(VerificationEmail.this, "Network Connectivity Issues.\n " +
                                            "Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
            });
            findViewById(R.id.tvLogIn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(VerificationEmail.this, LoginActivity.class));
                }
            });
        } else {
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = userAuthentication.getCurrentUser();
    }

    private void HideViewProperties(View view) {
        view.setEnabled(false);
        view.setVisibility(View.INVISIBLE);
    }

    private void EnableViewProperties(View view) {
        view.setEnabled(true);
        view.setVisibility(View.VISIBLE);
    }

    private void initialiseState() {
        etEmail.setEnabled(false);
        etConfirmPassword.setEnabled(false);
        tvSamePasswords.setVisibility(View.INVISIBLE);
        btnSubmit.setEnabled(false);
    }

    private void findViewsbyId() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etName = (EditText) findViewById(R.id.etName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassWord);
        tvSamePasswords = (TextView) findViewById(R.id.tvStatus);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
    }
}
