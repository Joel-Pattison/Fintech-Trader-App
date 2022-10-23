package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class SupportActivity extends AppCompatActivity {

    Spinner drpSupport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        drpSupport = findViewById(R.id.drpSupport);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Technical");
        arrayList.add("Broker");
        arrayList.add("Purchasing Stock");
        arrayList.add("Institution");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpSupport.setAdapter(arrayAdapter);
    }
}