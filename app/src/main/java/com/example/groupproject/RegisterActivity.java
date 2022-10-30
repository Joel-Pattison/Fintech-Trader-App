package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class RegisterActivity extends AppCompatActivity {

    Button btnCreateAccount;
    CheckBox checkBroker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        checkBroker = findViewById(R.id.checkBroker);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBroker.isChecked()) {
                    Intent send = new Intent(RegisterActivity.this, BrokerSelect.class);
                    startActivity(send);
                }
                else {
                    Intent send = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(send);
                }

            }
        });
    }
}