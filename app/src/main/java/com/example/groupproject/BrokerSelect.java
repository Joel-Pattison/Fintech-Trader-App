package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class BrokerSelect extends AppCompatActivity {

    Spinner drpInstitution, drpBroker;
    Button btnContinueBrokerSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broker_select);
        drpInstitution = findViewById(R.id.drpInstitution);
        drpBroker = findViewById(R.id.drpBroker);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Institution 1");
        arrayList.add("Institution 2");
        arrayList.add("Institution 3");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpInstitution.setAdapter(arrayAdapter);
        drpBroker.setAdapter(arrayAdapter);

        btnContinueBrokerSelect = findViewById(R.id.btnContinueBrokerSelect);

        btnContinueBrokerSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(BrokerSelect.this, HomeActivity.class);
                startActivity(send);
            }
        });
    }
}