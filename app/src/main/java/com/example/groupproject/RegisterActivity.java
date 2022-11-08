package com.example.groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    Button btnCreateAccount;
    CheckBox checkBroker;
    EditText txtEmail, txtName, txtPassword;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
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
        db = FirebaseFirestore.getInstance();

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

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User(name, email);

                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    /*Toast.makeText(RegisterActivity.this, "inside", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);*/
                    /*db.collection("users").document(userID).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Your account has been created", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Failed to create account, try again", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });*/

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Your account has been created", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    else{
                                        Toast.makeText(RegisterActivity.this, "Failed to create account, try again", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }else{
                    Toast.makeText(RegisterActivity.this, "Failed to create account, try again", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}