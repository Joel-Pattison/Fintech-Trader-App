package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class BrokerContactActivity extends AppCompatActivity {

    TextView txtBrokerEmail, txtBrokerPhone, txtBrokerName;
    FirebaseFirestore db;
    private FirebaseUser user;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broker_contact);

        txtBrokerEmail = findViewById(R.id.txtBrokerEmail);
        txtBrokerPhone = findViewById(R.id.txtBrokerPhone);
        txtBrokerName = findViewById(R.id.txtBrokerName);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //get the brokerUID field from the logged users account in firestore
        //use the brokerUID to get the broker's email and phone number from firestore
        //set the textviews to the broker's email and phone number
        db.collection("users").document(userID).get().addOnCompleteListener(task -> {
            String brokerUID = task.getResult().getString("brokerUID");
            if (brokerUID.trim().equals("none")){
                Toast.makeText(BrokerContactActivity.this, "No broker, please add one", Toast.LENGTH_SHORT).show();
                Intent send = new Intent(BrokerContactActivity.this, BrokerSelect.class);
                startActivity(send);
            }
            else{
                db.collection("brokers").document(brokerUID).get().addOnCompleteListener(task1 -> {
                    String brokerEmail = task1.getResult().getString("email");
                    String brokerPhone = task1.getResult().getString("phone");
                    String brokerName = task1.getResult().getString("name");
                    txtBrokerEmail.setText(brokerEmail);
                    txtBrokerPhone.setText(brokerPhone);
                    txtBrokerName.setText("Your broker is: " + brokerName);
                });
            }
        });
    }
}