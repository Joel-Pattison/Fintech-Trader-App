package com.example.groupproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainActivity extends AppCompatActivity {

    Button btnRegister, btnLogin;
    TextView txtEmail, txtPassword;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    ImageView btnGoogleSignIn;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    FirebaseFirestore db;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        txtEmail = findViewById(R.id.txtLoginEmail);
        txtPassword = findViewById(R.id.txtLoginPassword);
        progressBar = findViewById(R.id.progressLogin);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

        // txtEmail.setText("hello@gmail.com");
        // txtPassword.setText("hello123");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(send);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
                // Intent send = new Intent(MainActivity.this, HomeActivity.class);
                // startActivity(send);
            }
        });

        //getBodyText();

        //new DemoTask().execute("https://query1.finance.yahoo.com/v7/finance/quote?symbols=AAPL");

/*        Thread gfgThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Stock stock = YahooFinance.get("GOOG");
                    stock.print();
                    // Toast.makeText(MainActivity.this, stock.getQuote().getPrice().toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        gfgThread.start();*/

/*        Thread gfgThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    URL url = new URL("https://ip.jsontest.com/");
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode json = mapper.readTree(url);
                    String price = json.get("ip").toString();
                    txtEmail.setText(price);
                    //Toast.makeText(MainActivity.this, price, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        gfgThread.start();*/

/*        Thread gfgThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://ip.jsontest.com/");
                    Scanner sc = new Scanner(url.openStream());
                    StringBuffer sb = new StringBuffer();
                    while(sc.hasNext()) {
                        sb.append(sc.next());
                    }
                    String result = sb.toString();
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        gfgThread.start();*/
    }

    private void signInGoogle() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
            } catch (ApiException e) {
                Toast.makeText(this, "Error Signing In", Toast.LENGTH_SHORT).show();
            }

            //get the user details
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                //String name = account.getDisplayName();
                //String email = account.getEmail();
                //String photo = account.getPhotoUrl().toString();
                //Toast.makeText(this, name + " " + email + " " + photo, Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            userID = user.getUid();

                            //check if the user exists in the database
                            DocumentReference docRef = db.collection("users").document(userID);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (!document.exists()) {
                                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                            User newUser = new User(account.getDisplayName(), account.getEmail());
                                            db.collection("users").document(userID).set(newUser);
                                        }
                                        Intent send = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(send);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

/*                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            db.collection("users").document(userID)
                                    .set(newUser, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                            }
                                            Intent send = new Intent(MainActivity.this, HomeActivity.class);
                                            startActivity(send);
                                        }
                                    });*/

/*                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            db.collection("users").document(userID).set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                    Intent send = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(send);
                                }
                            });*/
                        }
                    }
                });
    }

    private void userLogin() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if(email.isEmpty()){
            txtEmail.setError("Please enter your email");
            txtEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("The email you entered is not valid");
            txtEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            txtPassword.setError("Please enter your password");
            txtPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            txtPassword.setError("Password length must be at least 6 characters");
            txtPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent send = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(send);
                } else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Failed to log in, please check your credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    private void getBodyText() {
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(builder.toString());
                            Double price = jsonObject.getJSONObject("quoteResponse").getJSONArray("result").getJSONObject(0).getDouble("regularMarketPrice");

                            txtEmail.setText(price.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }).start();
    }
}