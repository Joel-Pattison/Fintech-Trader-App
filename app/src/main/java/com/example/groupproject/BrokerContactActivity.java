package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class BrokerContactActivity extends AppCompatActivity {

    TextView txtBrokerEmail, txtBrokerPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broker_contact);

        txtBrokerEmail = findViewById(R.id.txtBrokerName);
        txtBrokerPhone = findViewById(R.id.txtBrokerEmail);

        txtBrokerEmail.setText("Email: Example@gmail.com");
        txtBrokerPhone.setText("Phone: 083 888 8888");
    }
}