package com.example.groupproject;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StockApi {
    public static String getPrice(String stock) {
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

            //lblToEdit.setText("€" + roundedValue.toString());
            return roundedValue.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
