package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BrokerSelect extends AppCompatActivity {

    Spinner drpInstitution, drpBroker;
    Button btnContinueBrokerSelect;
    FirebaseFirestore db;
    private FirebaseUser user;
    private DatabaseReference reference;
    Map<String, String> documentIds;
    Map<String, String> institutions;
    String selectedBrokerId;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broker_select);
        drpInstitution = findViewById(R.id.drpInstitution);
        drpBroker = findViewById(R.id.drpBroker);
        documentIds = new HashMap<>();
        institutions = new HashMap<>();

        db = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("brokers");

        btnContinueBrokerSelect = findViewById(R.id.btnContinueBrokerSelect);

        btnContinueBrokerSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(BrokerSelect.this, HomeActivity.class);
                startActivity(send);
            }
        });

/*        //get all brokers from the collection "brokers"
        db.collection("brokers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (Broker b : task.getResult().toObjects(Broker.class)) {
                    arrayList.add(b.getName());
                }
            } else {
                Toast.makeText(BrokerSelect.this, "Something went wrong while trying to communicate with the server", Toast.LENGTH_LONG).show();
            }
        });*/

        db.collection("brokers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //ArrayList<String> brokers = new ArrayList<>();
                for (int i = 0; i < task.getResult().size(); i++) {
                    //brokers.add(task.getResult().getDocuments().get(i).get("name").toString());
                    //documentIds.add(task.getResult().getDocuments().get(i).getId());
                    documentIds.put(task.getResult().getDocuments().get(i).get("name").toString(), task.getResult().getDocuments().get(i).getId());
                    institutions.put(task.getResult().getDocuments().get(i).get("name").toString(), task.getResult().getDocuments().get(i).get("institution").toString());
                    //ArrayAdapter<String> brokerAdapter = new ArrayAdapter<String>(BrokerSelect.this, android.R.layout.simple_spinner_item, brokers);
                    //brokerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //drpBroker.setAdapter(brokerAdapter);

                    ArrayList<String> uniqueInstitutions = new ArrayList<>();
                    for (String institution : institutions.values()) {
                        if (!uniqueInstitutions.contains(institution)) {
                            uniqueInstitutions.add(institution);
                        }
                    }

                    ArrayAdapter<String> institutionAdapter = new ArrayAdapter<String>(BrokerSelect.this, android.R.layout.simple_spinner_item, uniqueInstitutions);
                    institutionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    drpInstitution.setAdapter(institutionAdapter);
                }

                drpInstitution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedInstitution = drpInstitution.getSelectedItem().toString();
                        ArrayList<String> brokers = new ArrayList<>();
                        for (String broker : institutions.keySet()) {
                            if (institutions.get(broker).equals(selectedInstitution)) {
                                brokers.add(broker);
                            }
                        }
                        ArrayAdapter<String> brokerAdapter = new ArrayAdapter<String>(BrokerSelect.this, android.R.layout.simple_spinner_item, brokers);
                        brokerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        drpBroker.setAdapter(brokerAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                drpBroker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedBrokerId = documentIds.get(parent.getItemAtPosition(position).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            } else {
                Toast.makeText(BrokerSelect.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("JPMorgan Chase");
        arrayList.add("Goldman Sachs");
        arrayList.add("Morgan Stanley");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drpInstitution.setAdapter(arrayAdapter);
        drpBroker.setAdapter(arrayAdapter);

        // on click listener for the continue button which puts the broker id into the user's document under the field "brokerUID"
        btnContinueBrokerSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(userID).update("brokerUID", selectedBrokerId);
                Intent send = new Intent(BrokerSelect.this, HomeActivity.class);
                startActivity(send);
            }
        });
    }
}
