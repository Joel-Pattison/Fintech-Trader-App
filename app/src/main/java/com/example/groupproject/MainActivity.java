package com.example.groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        txtEmail = findViewById(R.id.txtLoginEmail);
        txtPassword = findViewById(R.id.txtLoginPassword);
        progressBar = findViewById(R.id.progressLogin);
        mAuth = FirebaseAuth.getInstance();

        txtEmail.setText("hello@gmail.com");
        txtPassword.setText("hello123");

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