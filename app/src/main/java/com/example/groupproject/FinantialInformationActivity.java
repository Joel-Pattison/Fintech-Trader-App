package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class FinantialInformationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner drpInfoType, drpInfoStock;
    WebView webChart;
    ArrayAdapter<CharSequence> tech_stock_adapter, crypto_asset_adapter;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finantial_information);

        drpInfoType = findViewById(R.id.drpInfoType);
        drpInfoStock = findViewById(R.id.drpInfoStock);
        webChart = findViewById(R.id.webChart);

        // ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListStockType);
        ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(this, R.array.stock_types, android.R.layout.simple_spinner_item);
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpInfoType.setAdapter(type_adapter);
        drpInfoType.setOnItemSelectedListener(this);

        tech_stock_adapter = ArrayAdapter.createFromResource(this, R.array.tech_stocks, android.R.layout.simple_spinner_item);
        tech_stock_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //drpInfoStock.setAdapter(tech_stock_adapter);
        drpInfoStock.setOnItemSelectedListener(this);

        crypto_asset_adapter = ArrayAdapter.createFromResource(this, R.array.crypto, android.R.layout.simple_spinner_item);
        crypto_asset_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        WebSettings webSettings = webChart.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webChart.setWebChromeClient(new WebChromeClient());
        // webChart.loadUrl("https://www.tradingview.com/chart/?symbol=NASDAQ%3AAAPL");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selected_item = adapterView.getItemAtPosition(i).toString();


        switch(selected_item){
            case "Technology Shares":
                drpInfoStock.setAdapter(tech_stock_adapter);
                break;
            case "Crypto":
                drpInfoStock.setAdapter(crypto_asset_adapter);
                break;
            case "Apple":
                webChart.loadUrl("https://www.tradingview.com/chart/?symbol=NASDAQ%3AAAPL");
                break;
            case "Microsoft":
                webChart.loadUrl("https://www.tradingview.com/chart/?symbol=NASDAQ%3AMSFT");
                break;
            case "Amazon":
                webChart.loadUrl("https://www.tradingview.com/chart/?symbol=NASDAQ%3AAMZN");
                break;
            case "Intel":
                webChart.loadUrl("https://www.tradingview.com/chart/?symbol=NASDAQ%3AINTC");
                break;
            case "AMD":
                webChart.loadUrl("https://www.tradingview.com/chart/?symbol=NASDAQ%3AAMD");
                break;
            case "Bitcoin":
                webChart.loadUrl("https://www.tradingview.com/chart/?symbol=BITSTAMP%3ABTCUSD");
                break;
            case "Ethereum":
                webChart.loadUrl("https://www.tradingview.com/chart/?symbol=BITSTAMP%3AETHUSD");
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}