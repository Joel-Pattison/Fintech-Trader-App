package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class TradeActivity extends AppCompatActivity {

    Spinner drpTradeType, drpType, drpStock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        drpTradeType = findViewById(R.id.drpTradeType);
        drpType = findViewById(R.id.drpType);
        drpStock = findViewById(R.id.drpStock);

        ArrayList<String> arrayListType = new ArrayList<>();
        arrayListType.add("Buy");
        arrayListType.add("Sell");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListType);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpTradeType.setAdapter(arrayAdapter);

        ArrayList<String> arrayListStockType = new ArrayList<>();
        arrayListStockType.add("Technology Shares");
        arrayListStockType.add("Crypto Assets");
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListStockType);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpType.setAdapter(arrayAdapter2);

        ArrayList<String> arrayListStock = new ArrayList<>();
        arrayListStock.add("AMD");
        arrayListStock.add("Intel");
        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListStock);
        arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpStock.setAdapter(arrayAdapter3);
    }
}