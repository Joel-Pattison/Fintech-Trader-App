package com.example.groupproject;

import android.media.Rating;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {

    public String name, email, brokerUID;
    public Map<String, String> balance = new HashMap<>();
    public Map<String, String> techStocks = new HashMap<>();
    public Map<String, String> crypto = new HashMap<>();
    public ArrayList<Ticket> tickets = new ArrayList<>();
    public ArrayList<Review> reviews = new ArrayList<>();

    public User(){

    }

    public User(String name, String email){
        this.name = name;
        this.email = email;
        this.brokerUID = "none";
        balance.put("euroBalance", "100");
        balance.put("brokerEuroBalance", "500");
        techStocks.put("AMD", "5");
        techStocks.put("Apple", "10");
        crypto.put("Bitcoin", "0.02");
    }

    public void addTicket(Ticket t){
        this.tickets.add(t);
    }
}
