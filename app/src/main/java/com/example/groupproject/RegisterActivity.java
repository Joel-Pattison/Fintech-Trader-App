package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    Button btnCreateAccount;
    CheckBox checkBroker;
    EditText txtEmail, txtName, txtPassword;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        checkBroker = findViewById(R.id.checkBroker);
        txtEmail = findViewById(R.id.txtRegisterEmail);
        txtName = findViewById(R.id.txtRegisterName);
        txtPassword = findViewById(R.id.txtRegisterPassword);
        progressBar = findViewById(R.id.progressRegister);
        mAuth = FirebaseAuth.getInstance();

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBroker.isChecked()) {
                    registerUser();
                    Intent send = new Intent(RegisterActivity.this, BrokerSelect.class);
                    startActivity(send);
                }
                else {
                    registerUser();
                    // Intent send = new Intent(RegisterActivity.this, HomeActivity.class);
                    // startActivity(send);
                }

            }
        });
    }

    private void registerUser() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String name = txtName.getText().toString().trim();

        if(email.isEmpty()){
            txtEmail.setError("Please enter an email");
            txtEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("The email you provided is invalid");
            txtEmail.requestFocus();
            return;
        }

        if(name.isEmpty()){
            txtName.setError("Please enter your name");
            txtName.requestFocus();
            return;
        }

        if(password.isEmpty()){
            txtPassword.setError("Please enter a password");
            txtPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            txtPassword.setError("Password must include a minimum of 6 characters");
            txtPassword.requestFocus();
            return;
        }
    }
}