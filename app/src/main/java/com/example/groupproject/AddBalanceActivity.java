package com.example.groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AddBalanceActivity extends AppCompatActivity {

    private static final String YOUR_CLIENT_ID = "AYFpCIrqcwNop2ULS5_Cwp9kSyi8pIsOhThRB521awH4d-xYDvnkRsqvYYccirxSQPk1KG-jvBiZbnYy";
/*    PaymentButtonContainer payPalButton;*/
    Button btnAddFunds;
    PayPalCheckout payPalCheckout;
    FirebaseFirestore db;
    private String userID;
    User curUserProfile;
    EditText txtTopupAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance);

        btnAddFunds = findViewById(R.id.btnAddFunds);
        txtTopupAmount = findViewById(R.id.txtTopupAmount);

        getCurUserProfile();

        btnAddFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txtTopupAmount.getText().toString().isEmpty()) {
                    txtTopupAmount.setError("Please enter a valid amount");
                    txtTopupAmount.requestFocus();
                } else{
                    addBalance();
                }
            }
        });

/*        PayPalCheckout.setConfig(new CheckoutConfig(
                getApplication(),
                YOUR_CLIENT_ID,
                Environment.SANDBOX,
                CurrencyCode.USD,
                UserAction.PAY_NOW
        ));*/

/*        payPalButton = findViewById(R.id.paymentButtonContainer);

        payPalButton.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value("10.00")
                                                        .build()
                                        )
                                        .build()
                        );
                        Order order = new Order(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                            }
                        });
                    }
                }
        );*/
    }

    private void addBalance() {
        String balanceToAdd = txtTopupAmount.getText().toString();
        double balanceToAddDouble = Double.parseDouble(balanceToAdd);
        double curBalance = Double.parseDouble(curUserProfile.balance.get("euroBalance"));
        double newBalance = curBalance + balanceToAddDouble;
        curUserProfile.balance.put("euroBalance", String.valueOf(newBalance));
        setCurUserProfile();
    }

    private void getCurUserProfile(){
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        db.collection("users").document(userID)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null){
                        curUserProfile = task.getResult().toObject(User.class);
                    }else{
                        Toast.makeText(AddBalanceActivity.this, "Something went wrong when trying to get your data.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setCurUserProfile(){
        db.collection("users").document(userID).set(curUserProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AddBalanceActivity.this, "Successfully added balance", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(AddBalanceActivity.this, "Failed to add balance", Toast.LENGTH_SHORT).show();
                }
                Intent send = new Intent(AddBalanceActivity.this, HomeActivity.class);
                startActivity(send);
            }
        });
    }
}