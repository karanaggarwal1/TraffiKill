package com.example.karan.traffikill.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karan.traffikill.R;

public class SignUpActivity extends AppCompatActivity {
    EditText etPhoneEmail;
    Button btnRegister;
    TextView tvLogIn;

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etPhoneEmail = (EditText) findViewById(R.id.et_phone_email);
        tvLogIn = (TextView) findViewById(R.id.tvLogIn);
        btnRegister = (Button) findViewById(R.id.btnNext);

        etPhoneEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    btnRegister.setEnabled(false);
                    btnRegister.setBackgroundColor(getResources().getColor(R.color.buttonNotSelected));
                } else {
                    btnRegister.setEnabled(true);
                    btnRegister.setBackgroundColor(getResources().getColor(R.color.buttonSelected));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent outgoingIntent;
                if (isValidEmail(etPhoneEmail.getText())) {
                    outgoingIntent = new Intent(SignUpActivity.this, VerificationEmail.class);
                    outgoingIntent.putExtra("type", "email");
                    outgoingIntent.putExtra("email", etPhoneEmail.getText().toString());
                } else if (isValidMobile(etPhoneEmail.getText())) {
                    outgoingIntent = new Intent(SignUpActivity.this, VerificationPhone.class);
                    outgoingIntent.putExtra("type", "phone");
                    outgoingIntent.putExtra("phone", String.valueOf(etPhoneEmail.getText().toString()));
                } else {
                    Toast.makeText(SignUpActivity.this, "Please enter valid Email/Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (outgoingIntent != null) {
                    startActivity(outgoingIntent);
                }
            }
        });

    }

    private boolean isValidMobile(CharSequence phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
}
