package com.example.groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RatingActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private String userID;
    private FirebaseUser user;
    private RatingBar ratingBar;
    private EditText txtReview;
    User curUserProfile;
    Button btnSubmitReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        ratingBar = findViewById(R.id.ratingBar);
        txtReview = findViewById(R.id.txtReview);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        getCurUserProfile();

        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRating();
                Intent intent = new Intent(RatingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void createRating(){
        curUserProfile.reviews.add(new Review(txtReview.getText().toString(), ratingBar.getRating()));
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
                        Toast.makeText(RatingActivity.this, "Something went wrong when trying to get your data.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setCurUserProfile(){
        db.collection("users").document(userID).set(curUserProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RatingActivity.this, "Review Submitted", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RatingActivity.this, "Review failed to submit", Toast.LENGTH_SHORT).show();
                }
                Intent send = new Intent(RatingActivity.this, HomeActivity.class);
                startActivity(send);
            }
        });
    }
}