package com.example.groupproject.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.groupproject.User;
import com.example.groupproject.databinding.FragmentHomeBinding;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.math.RoundingMode;
import java.math.BigDecimal;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private TextView lblWelcome;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private TextView txtTechName1, txtTechName2, txtTechName3, txtTechName4, txtTechName5;
    private TextView txtShareAmount1, txtShareAmount2, txtShareAmount3, txtShareAmount4, txtShareAmount5;
    private TextView txtSharePrice1, txtSharePrice2, txtSharePrice3, txtSharePrice4, txtSharePrice5;
    private TextView txtCryptoName1, txtCryptoName2;
    private TextView txtCryptoAmount1, txtCryptoAmount2;
    private TextView txtCryptoPrice1, txtCryptoPrice2;
    private TextView lblBrokerBalance, lblBalance;
    FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        lblWelcome = binding.lblWelcome;
        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        txtTechName1 = binding.txtTechName1;
        txtTechName2 = binding.txtTechName2;
        txtTechName3 = binding.txtTechName3;
        txtTechName4 = binding.txtTechName4;
        txtTechName5 = binding.txtTechName5;

        txtShareAmount1 = binding.txtShareAmount1;
        txtShareAmount2 = binding.txtShareAmount2;
        txtShareAmount3 = binding.txtShareAmount3;
        txtShareAmount4 = binding.txtShareAmount4;
        txtShareAmount5 = binding.txtShareAmount5;

        txtSharePrice1 = binding.txtSharePrice1;
        txtSharePrice2 = binding.txtSharePrice2;
        txtSharePrice3 = binding.txtSharePrice3;
        txtSharePrice4 = binding.txtSharePrice4;
        txtSharePrice5 = binding.txtSharePrice5;

        txtCryptoName1 = binding.txtCryptoName1;
        txtCryptoName2 = binding.txtCryptoName2;

        txtCryptoAmount1 = binding.txtCryptoAmount1;
        txtCryptoAmount2 = binding.txtCryptoAmount2;

        txtCryptoPrice1 = binding.txtCryptoPrice1;
        txtCryptoPrice2 = binding.txtCryptoPrice2;

        lblBalance = binding.lblBalance;
        lblBrokerBalance = binding.lblBrokerBalance;

        DocumentReference docRef = db.collection("users").document(userID);

        db.collection("users").document(userID)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null){
                        User curUserProfile = task.getResult().toObject(User.class);

                        String name = curUserProfile.name;
                        lblWelcome.setText("Welcome back, " + name + "!");

                        displayStocks(curUserProfile);
                        displayCrypto(curUserProfile);
                        displayBalance(curUserProfile);
                        //getPrices();
                        //getStockPrices();
                    }else{
                        Toast.makeText(getActivity(), "Something went wrong when trying to get your data.", Toast.LENGTH_LONG).show();
                    }
                });

        /*reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userProfile = dataSnapshot.getValue(User.class);

                if(userProfile != null){
                    String name = userProfile.name;
                    lblWelcome.setText("Welcome back, " + name + "!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Something went wrong when trying to get your data.", Toast.LENGTH_LONG).show();
            }
        });*/

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void displayBalance(User user) {
        Set keys = user.crypto.keySet();
        String balance = user.balance.get("euroBalance");
        String brokerBalance = user.balance.get("brokerEuroBalance");

        lblBalance.setText(lblBalance.getText() + "€" + balance);
        lblBrokerBalance.setText(lblBrokerBalance.getText() + "€" + brokerBalance);

    }

    public void displayCrypto(User user) {
        Set keys = user.crypto.keySet();
        int keyIndex = 1;
        for (Object key : keys) {
            switch (keyIndex) {
                case 1:
                    txtCryptoName1.setText(key.toString());
                    txtCryptoAmount1.setText(user.crypto.get(key));
                    Thread stock1 = new Thread(() -> getPrice(key.toString(), txtCryptoPrice1, user.crypto.get(key)));
                    stock1.start();
                    break;
                case 2:
                    txtCryptoName2.setText(key.toString());
                    txtCryptoAmount2.setText(user.crypto.get(key));
                    Thread stock2 = new Thread(() -> getPrice(key.toString(), txtCryptoPrice2, user.crypto.get(key)));
                    stock2.start();
                    break;
            }
            keyIndex++;
        }
    }

    public void displayStocks(User user) {
        //looping through all the keys to get the stock names then putting them on screen
        Set keys = user.techStocks.keySet();
        int keyIndex = 1;
        for (Object key : keys) {
            switch (keyIndex){
                case 1:
                    txtTechName1.setText(key.toString());
                    txtShareAmount1.setText(user.techStocks.get(key));
                    Thread stock1 = new Thread(() -> getPrice(key.toString(), txtSharePrice1, user.techStocks.get(key)));
                    stock1.start();
                    break;
                case 2:
                    txtTechName2.setText(key.toString());
                    txtShareAmount2.setText(user.techStocks.get(key));
                    Thread stock2 = new Thread(() -> getPrice(key.toString(), txtSharePrice2, user.techStocks.get(key)));
                    stock2.start();
                    break;
                case 3:
                    txtTechName3.setText(key.toString());
                    txtShareAmount3.setText(user.techStocks.get(key));
                    Thread stock3 = new Thread(() -> getPrice(key.toString(), txtSharePrice3, user.techStocks.get(key)));
                    stock3.start();
                    break;
                case 4:
                    txtTechName4.setText(key.toString());
                    txtShareAmount4.setText(user.techStocks.get(key));
                    Thread stock4 = new Thread(() -> getPrice(key.toString(), txtSharePrice4, user.techStocks.get(key)));
                    stock4.start();
                    break;
                case 5:
                    txtTechName5.setText(key.toString());
                    txtShareAmount5.setText(user.techStocks.get(key));
                    Thread stock5 = new Thread(() -> getPrice(key.toString(), txtSharePrice5, user.techStocks.get(key)));
                    stock5.start();
                    break;
            }
            keyIndex++;
        }

        /*//looping through all the values to get the stock amount then putting them on screen
        Collection<String> values = user.techStocks.values();
        int valueIndex = 1;
        for(String value: values){
            switch (valueIndex){
                case 1:
                    txtShareAmount1.setText(value);
                    break;
                case 2:
                    txtShareAmount2.setText(value);
                    break;
                case 3:
                    txtShareAmount3.setText(value);
                    break;
                case 4:
                    txtShareAmount4.setText(value);
                    break;
                case 5:
                    txtShareAmount5.setText(value);
                    break;
            }
            valueIndex++;
        }

        keys = user.techStocks.keySet();
        for (Object key : keys) {
            if (key.toString() == "apple"){

            }
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getPriceAutoUpdate(String stock, TextView lblToEdit, String amount) {
        while (true) {
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
                Double value = price * Double.parseDouble(amount);

                lblToEdit.setText("€" + value.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPrice(String stock, TextView lblToEdit, String amount) {
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
            Double value = price * Double.parseDouble(amount);
            BigDecimal roundedValue = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);

            lblToEdit.setText("€" + roundedValue.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getPrices() {
        String url= "";
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                String url="https://query1.finance.yahoo.com/v7/finance/quote?symbols=AAPL";

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

                    txtSharePrice1.setText(price.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}