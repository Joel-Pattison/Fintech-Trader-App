package com.example.groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SupportActivity extends AppCompatActivity {

    Spinner drpSupport;
    EditText txtSupportTicketMessage;
    Button btnSubmitTicket;
    FirebaseFirestore db;
    private String userID;
    private FirebaseUser user;
    User curUserProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        drpSupport = findViewById(R.id.drpSupport);
        btnSubmitTicket = findViewById(R.id.btnSubmitTicket);
        txtSupportTicketMessage = findViewById(R.id.txtSupportTicketMessage);
        getCurUserProfile();

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Technical");
        arrayList.add("Broker");
        arrayList.add("Purchasing Stock");
        arrayList.add("Institution");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpSupport.setAdapter(arrayAdapter);

        btnSubmitTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTicket();
                Intent send = new Intent(SupportActivity.this, HomeActivity.class);
                startActivity(send);
            }
        });
    }

    public void createTicket(){
        curUserProfile.tickets.add(new Ticket("open", drpSupport.getSelectedItem().toString(), txtSupportTicketMessage.getText().toString()));
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
                    }else{
                        Toast.makeText(SupportActivity.this, "Something went wrong when trying to get your data.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setCurUserProfile(){
        db.collection("users").document(userID).set(curUserProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SupportActivity.this, "Ticket Created", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SupportActivity.this, "Failed to create ticket", Toast.LENGTH_SHORT).show();
                }
                Intent send = new Intent(SupportActivity.this, HomeActivity.class);
                startActivity(send);
            }
        });
    }
}