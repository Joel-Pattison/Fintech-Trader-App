package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class PredictedFuturesActivity extends AppCompatActivity {

    Spinner drpType, drpStock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predicted_futures);

        drpType = findViewById(R.id.drpFutureType);
        drpStock = findViewById(R.id.drpFutureStock);

        ArrayList<String> arrayStockType = new ArrayList<>();
        arrayStockType.add("Technology Shares");
        arrayStockType.add("Crypto Assets");
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayStockType);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpType.setAdapter(arrayAdapter1);

        ArrayList<String> arrayListStock = new ArrayList<>();
        arrayListStock.add("AMD");
        arrayListStock.add("Intel");
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListStock);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpStock.setAdapter(arrayAdapter2);
    }
}