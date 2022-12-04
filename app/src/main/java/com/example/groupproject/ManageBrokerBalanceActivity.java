package com.example.groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ManageBrokerBalanceActivity extends AppCompatActivity {

    Spinner drpTransferMethod;
    TextView txtTransferAmount;
    FirebaseFirestore db;
    private String userID;
    private FirebaseUser user;
    User curUserProfile;
    Button btnTransfer;
    String selected_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_broker_balance);

        drpTransferMethod = findViewById(R.id.drpTransferMethod);
        txtTransferAmount = findViewById(R.id.txtTransferAmount);
        btnTransfer = findViewById(R.id.btnTransfer);

        getCurUserProfile();
        ArrayList<String> arrayListType = new ArrayList<>();
        arrayListType.add("Transfer to broker account");
        arrayListType.add("Transfer from broker account");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListType);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpTransferMethod.setAdapter(arrayAdapter);

        drpTransferMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_item = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtTransferAmount.getText().toString().isEmpty()) {
                    txtTransferAmount.setError("Please enter an amount to transfer");
                    txtTransferAmount.requestFocus();
                }
                else{
                    switch(selected_item) {
                        case "Transfer to broker account":
                            transferToBrokerAccount();
                            break;
                        case "Transfer from broker account":
                            transferFromBrokerAccount();
                            break;
                    }

                }
            }
        });
    }

    private void transferToBrokerAccount() {
        String transferAmount = txtTransferAmount.getText().toString();
        double transferAmountDouble = Double.parseDouble(transferAmount);
        double curBalance = Double.parseDouble(curUserProfile.balance.get("euroBalance"));
        double newBalance = curBalance - transferAmountDouble;
        double curBrokerBalance = Double.parseDouble(curUserProfile.balance.get("brokerEuroBalance"));
        double newBrokerBalance = curBrokerBalance + transferAmountDouble;
        curUserProfile.balance.put("euroBalance", String.valueOf(newBalance));
        curUserProfile.balance.put("brokerEuroBalance", String.valueOf(newBrokerBalance));
        setCurUserProfile();
    }

    private void transferFromBrokerAccount() {
        String transferAmount = txtTransferAmount.getText().toString();
        double transferAmountDouble = Double.parseDouble(transferAmount);
        double curBalance = Double.parseDouble(curUserProfile.balance.get("euroBalance"));
        double newBalance = curBalance + transferAmountDouble;
        double curBrokerBalance = Double.parseDouble(curUserProfile.balance.get("brokerEuroBalance"));
        double newBrokerBalance = curBrokerBalance - transferAmountDouble;
        curUserProfile.balance.put("euroBalance", String.valueOf(newBalance));
        curUserProfile.balance.put("brokerEuroBalance", String.valueOf(newBrokerBalance));
        setCurUserProfile();
    }

    private void getCurUserProfile(){
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        db.collection("users").document(userID)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null){
                        curUserProfile = task.getResult().toObject(User.class);

                        if (curUserProfile.brokerUID.trim().equals("none")) {
                            Toast.makeText(ManageBrokerBalanceActivity.this, "You do not have a broker, please select one", Toast.LENGTH_SHORT).show();
                            Intent send = new Intent(ManageBrokerBalanceActivity.this, BrokerSelect.class);
                            startActivity(send);
                        }
                    }else{
                        Toast.makeText(ManageBrokerBalanceActivity.this, "Something went wrong when trying to get your data.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setCurUserProfile(){
        db.collection("users").document(userID).set(curUserProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ManageBrokerBalanceActivity.this, "Transfer Successful", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ManageBrokerBalanceActivity.this, "Transfer Unsuccessful", Toast.LENGTH_SHORT).show();
                }
                Intent send = new Intent(ManageBrokerBalanceActivity.this, HomeActivity.class);
                startActivity(send);
            }
        });
    }
}