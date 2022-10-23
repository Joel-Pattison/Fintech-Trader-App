package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class FinantialInformationActivity extends AppCompatActivity {

    Spinner drpInfoTradeType, drpInfoType, drpInfoStock;
    WebView webChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finantial_information);

        drpInfoType = findViewById(R.id.drpInfoType);
        drpInfoStock = findViewById(R.id.drpInfoStock);
        webChart = findViewById(R.id.webChart);

        ArrayList<String> arrayListStockType = new ArrayList<>();
        arrayListStockType.add("Technology Shares");
        arrayListStockType.add("Crypto Assets");
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListStockType);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpInfoType.setAdapter(arrayAdapter1);

        ArrayList<String> arrayListStock = new ArrayList<>();
        arrayListStock.add("AMD");
        arrayListStock.add("Intel");
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListStock);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpInfoStock.setAdapter(arrayAdapter2);

        WebSettings webSettings = webChart.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webChart.setWebChromeClient(new WebChromeClient());
        webChart.loadUrl("https://www.tradingview.com/chart/?symbol=NASDAQ%3AAMD");
    }
}