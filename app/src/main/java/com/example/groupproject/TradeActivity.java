package com.example.groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class TradeActivity extends AppCompatActivity{

    Spinner drpTradeType, drpType, drpStock;
    ArrayAdapter<CharSequence> tech_stock_adapter, crypto_asset_adapter;
    ArrayAdapter<String> user_tech_stock_adapter, user_crypto_asset_adapter;
    FirebaseFirestore db;
    private String userID;
    private FirebaseUser user;
    User curUserProfile;
    String curStockPrice, curTotalPrice, curSelectedStock, curSelectedStockType, curStockAmount;
    EditText txtStockAmount;
    TextView txtPrice;
    Button btnCompleteTrade;
    TextView txtStockAmountOwned;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        drpTradeType = findViewById(R.id.drpTradeType);
        drpType = findViewById(R.id.drpType);
        drpStock = findViewById(R.id.drpStock);
        txtStockAmount = findViewById(R.id.txtStockAmount);
        txtPrice = findViewById(R.id.txtPrice);
        btnCompleteTrade = findViewById(R.id.btnCompleteTrade);
        txtStockAmountOwned = findViewById(R.id.txtStockAmountOwned);
        // txtStockAmountOwned.setVisibility(View.GONE);

        curStockPrice = "null";
        curTotalPrice = "null";
        curSelectedStock= "null";

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        db.collection("users").document(userID)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null){
                        curUserProfile = task.getResult().toObject(User.class);

                        Set<String> keysetTech = curUserProfile.techStocks.keySet();
                        String[] arrayTech = (String[]) keysetTech.toArray(new String[0]);
                        user_tech_stock_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayTech);
                        user_tech_stock_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        Set<String> keysetCrypto = curUserProfile.crypto.keySet();
                        String[] arrayCrypto = (String[]) keysetCrypto.toArray(new String[0]);
                        user_crypto_asset_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayCrypto);
                        user_crypto_asset_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        drpStock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                //String selected_item = parent.getItemAtPosition(position).toString();
                                Thread stock1 = new Thread(() -> getPrice(parent.getItemAtPosition(position).toString()));
                                stock1.start();
                                curSelectedStock = parent.getItemAtPosition(position).toString();
                                txtStockAmount.setText("");
                                if(drpTradeType.getSelectedItem().toString().equals("Sell")){
                                    if(curSelectedStockType.equals("Technology Shares")){
                                        txtStockAmountOwned.setText("You own " + curUserProfile.techStocks.get(curSelectedStock) + " shares of " + curSelectedStock);
                                    }else{
                                        txtStockAmountOwned.setText("You own " + curUserProfile.crypto.get(curSelectedStock) + " shares of " + curSelectedStock);
                                    }
                                }
                                else if(drpTradeType.getSelectedItem().toString().equals("Buy")){
                                    txtStockAmountOwned.setText("Balance: €" + curUserProfile.balance.get("euroBalance"));
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }else{
                        Toast.makeText(TradeActivity.this, "Something went wrong when trying to get your data.", Toast.LENGTH_LONG).show();
                    }
                });

        ArrayList<String> arrayListType = new ArrayList<>();
        arrayListType.add("Buy");
        arrayListType.add("Sell");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListType);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpTradeType.setAdapter(arrayAdapter);

        ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(this, R.array.stock_types, android.R.layout.simple_spinner_item);
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpType.setAdapter(type_adapter);


        tech_stock_adapter = ArrayAdapter.createFromResource(this, R.array.tech_stocks, android.R.layout.simple_spinner_item);
        tech_stock_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        drpStock.setAdapter(tech_stock_adapter);

        crypto_asset_adapter = ArrayAdapter.createFromResource(this, R.array.crypto, android.R.layout.simple_spinner_item);
        crypto_asset_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        drpTradeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(drpTradeType.getSelectedItem().toString().equals("Buy")){
                    btnCompleteTrade.setText("Buy");

                    if(drpType.getSelectedItem().toString().equals("Technology Shares")) {
                        drpStock.setAdapter(tech_stock_adapter);
                    }
                    else if(drpType.getSelectedItem().toString().equals("Crypto Assets")) {
                        drpStock.setAdapter(crypto_asset_adapter);
                    }
                }else{
                    btnCompleteTrade.setText("Sell");

                    if(drpType.getSelectedItem().toString().equals("Technology Shares")) {
                        drpStock.setAdapter(user_tech_stock_adapter);
                    }
                    else if(drpType.getSelectedItem().toString().equals("Crypto Assets")) {
                        drpStock.setAdapter(user_crypto_asset_adapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        drpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_item = parent.getItemAtPosition(position).toString();
                //Toast.makeText(TradeActivity.this, "inside", Toast.LENGTH_SHORT).show();

                switch(selected_item) {
                    case "Technology Shares":
                        if(drpTradeType.getSelectedItem().toString().equals("Buy")) {
                            drpStock.setAdapter(tech_stock_adapter);
                        }else{
                            drpStock.setAdapter(user_tech_stock_adapter);
                        }
                        curSelectedStockType = "Technology Shares";
                        break;
                    case "Crypto":
                        if(drpTradeType.getSelectedItem().toString().equals("Buy")) {
                            drpStock.setAdapter(crypto_asset_adapter);
                        }else{
                            drpStock.setAdapter(user_crypto_asset_adapter);
                        }
                        curSelectedStockType = "Crypto";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtStockAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(curStockPrice != "null" && s.length() != 0){
                    // limit the entered amount to the amount of the stock the user has, convert the value in the users profile to a double
                    if(drpTradeType.getSelectedItem().toString().equals("Sell")){
                        if(curSelectedStockType.equals("Technology Shares")){
                            if(Double.parseDouble(s.toString()) > Double.parseDouble(curUserProfile.techStocks.get(curSelectedStock))){
                                txtStockAmount.setText(String.valueOf(curUserProfile.techStocks.get(curSelectedStock)));
                                s = curUserProfile.techStocks.get(curSelectedStock);
                            }
                        }else{
                            if(Double.parseDouble(s.toString()) > Double.parseDouble(curUserProfile.crypto.get(curSelectedStock))){
                                txtStockAmount.setText(String.valueOf(curUserProfile.crypto.get(curSelectedStock)));
                                s = curUserProfile.crypto.get(curSelectedStock);
                            }
                        }
                    }
                    // make an else which compares the current total price with the user balance
                    else if(drpTradeType.getSelectedItem().toString().equals("Buy")){
                        if(curSelectedStockType.equals("Technology Shares")){
                            if(Double.parseDouble(s.toString()) * Double.parseDouble(curStockPrice) > Double.parseDouble(curUserProfile.balance.get("euroBalance"))){
                                txtStockAmount.setText(String.valueOf(Double.parseDouble(curUserProfile.balance.get("euroBalance")) / Double.parseDouble(curStockPrice)));
                                s = String.valueOf(Double.parseDouble(curUserProfile.balance.get("euroBalance")) / Double.parseDouble(curStockPrice));
                            }
                        }else{
                            if(Double.parseDouble(s.toString()) * Double.parseDouble(curStockPrice) > Double.parseDouble(curUserProfile.balance.get("euroBalance"))){
                                txtStockAmount.setText(String.valueOf(Double.parseDouble(curUserProfile.balance.get("euroBalance")) / Double.parseDouble(curStockPrice)));
                                s = String.valueOf(Double.parseDouble(curUserProfile.balance.get("euroBalance")) / Double.parseDouble(curStockPrice));
                            }
                        }
                    }

                    double price = Double.parseDouble(s.toString()) * Double.parseDouble(curStockPrice);
                    BigDecimal roundedPrice = new BigDecimal(price).setScale(2, RoundingMode.HALF_DOWN);
                    curTotalPrice = roundedPrice.toString();
                    String priceText = "€" + roundedPrice.toString();
                    curStockAmount = s.toString();
                    txtPrice.setText(priceText);
                }
                else{
                    txtPrice.setText("€");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnCompleteTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buyOrSell = drpTradeType.getSelectedItem().toString();
                switch(buyOrSell){
                    case("Buy"):
                        buyStock();
                        break;
                    case("Sell"):
                        sellStock();
                        break;
                }
            }
        });

    }

    private void buyStock(){
        //CHECKING THAT THE USER HAS SELECTED A STOCK AND ENTERED A STOCK AMOUNT
        if(curTotalPrice != "null" && curSelectedStock != "null"){
            double userBalance = Double.parseDouble(curUserProfile.balance.get("euroBalance"));
            //CHECKING TO SEE IF USER HAS ENOUGH BALANCE
            if (userBalance >= Double.parseDouble(curTotalPrice)){
                switch(curSelectedStockType){
                    case "Technology Shares":
                        Map<String, String> stocks = curUserProfile.techStocks;
                        if(stocks.get(curSelectedStock) != null){
                            String userStockAmount = stocks.get(curSelectedStock);
                            double newUserStockAmount = Double.parseDouble(userStockAmount) + Double.parseDouble(curStockAmount);
                            //double price = Double.parseDouble(amount) + Double.parseDouble(curTotalPrice);
                            //BigDecimal roundedPrice = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
                            stocks.put(curSelectedStock, String.valueOf(newUserStockAmount));
                        }
                        else{
                            stocks.put(curSelectedStock, curStockAmount);
                        }
                        break;
                    case "Crypto":
                        Map<String, String> crypto = curUserProfile.crypto;
                        if(crypto.get(curSelectedStock) != null){
                            String userStockAmount = crypto.get(curSelectedStock);
                            double newUserStockAmount = Double.parseDouble(userStockAmount) + Double.parseDouble(curStockAmount);
                            //double price = Double.parseDouble(amount) + Double.parseDouble(curTotalPrice);
                            //BigDecimal roundedPrice = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
                            crypto.put(curSelectedStock, String.valueOf(newUserStockAmount));
                        }
                        else{
                            crypto.put(curSelectedStock, curStockAmount);
                        }
                        break;
                }

                Double newUserBalance = userBalance - Double.parseDouble(curTotalPrice);

                BigDecimal roundedBalance = new BigDecimal(newUserBalance).setScale(2, RoundingMode.HALF_UP);

                curUserProfile.balance.put("euroBalance", String.valueOf(roundedBalance));

                db.collection("users").document(userID).set(curUserProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(TradeActivity.this, "You purchased " + curSelectedStock + " stock worth " + curTotalPrice, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(TradeActivity.this, "Failed to complete complete the purchase", Toast.LENGTH_SHORT).show();
                        }
                        Intent send = new Intent(TradeActivity.this, HomeActivity.class);
                        startActivity(send);
                    }
                });
            }
            else{
                Toast.makeText(TradeActivity.this, "Not enough balance to make this purchase", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sellStock() {
        if(curSelectedStock != "null" && curStockAmount != "null"){
            boolean toBreak = false;
            switch(curSelectedStockType){
                case "Technology Shares":
                    if(curUserProfile.techStocks.get(curSelectedStock) == null){
                        Toast.makeText(TradeActivity.this, "You do not own this stock", Toast.LENGTH_SHORT).show();
                        toBreak = true;
                        break;
                    }

                    Map<String, String> stocks = curUserProfile.techStocks;

                    String userStockAmount = stocks.get(curSelectedStock);
                    double newUserStockAmount = Double.parseDouble(userStockAmount) - Double.parseDouble(curStockAmount);
                    //double price = Double.parseDouble(amount) + Double.parseDouble(curTotalPrice);
                    //BigDecimal roundedPrice = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
                    if(newUserStockAmount == 0){
                        stocks.remove(curSelectedStock);
                    }
                    else{
                        stocks.put(curSelectedStock, String.valueOf(newUserStockAmount));
                    }
                    break;
                case "Crypto":
                    if(curUserProfile.crypto.get(curSelectedStock) == null){
                        Toast.makeText(TradeActivity.this, "You do not own this crypto", Toast.LENGTH_SHORT).show();
                        toBreak = true;
                        break;
                    }
                    Map<String, String> crypto = curUserProfile.crypto;

                    String userCryptoAmount = crypto.get(curSelectedStock);
                    double newUserCryptoAmount = Double.parseDouble(userCryptoAmount) - Double.parseDouble(curStockAmount);
                    //double price = Double.parseDouble(amount) + Double.parseDouble(curTotalPrice);
                    BigDecimal roundedPrice = new BigDecimal(newUserCryptoAmount).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros();
                    if(newUserCryptoAmount == 0){
                        crypto.remove(curSelectedStock);
                    }
                    else{
                        crypto.put(curSelectedStock, roundedPrice.toString());
                    }
                    break;
            }

            if(!toBreak){
                double userBalance = Double.parseDouble(curUserProfile.balance.get("euroBalance"));

                double newUserBalance = userBalance + Double.parseDouble(curTotalPrice);

                BigDecimal roundedBalance = new BigDecimal(newUserBalance).setScale(2, RoundingMode.HALF_UP);

                curUserProfile.balance.put("euroBalance", String.valueOf(roundedBalance));

                db.collection("users").document(userID).set(curUserProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(TradeActivity.this, "You successfully sold " + curSelectedStock + " stock worth " + curTotalPrice, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(TradeActivity.this, "Failed to complete complete the sale", Toast.LENGTH_SHORT).show();
                        }
                        Intent send = new Intent(TradeActivity.this, HomeActivity.class);
                        startActivity(send);
                    }
                });

            }
        }
    }

    private void getPrice(String stock) {
        String url= "https://query1.finance.yahoo.com/v7/finance/quote?symbols=";
        switch(stock){
            case "Apple":
                url += "AAPL";
                break;
            case "Microsoft":
                url += "MSFT";
                break;
            case "Amazon":
                url += "AMZN";
                break;
            case "Intel":
                url += "INTC";
                break;
            case "AMD":
                url += "AMD";
                break;
            case "Bitcoin":
                url += "BTC-USD";
                break;
            case "Etherium":
                url += "ETH-USD";
                break;
        }

        final StringBuilder builder = new StringBuilder();

        try {
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();

            Element body = doc.body();
            builder.append(body.text());

        } catch (Exception e) {
            builder.append("Error : ").append(e.getMessage()).append("\n");
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(builder.toString());
            Double price = jsonObject.getJSONObject("quoteResponse").getJSONArray("result").getJSONObject(0).getDouble("regularMarketPrice");
            //Double value = price * Double.parseDouble(amount);
            BigDecimal roundedValue = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);

            curStockPrice = roundedValue.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}