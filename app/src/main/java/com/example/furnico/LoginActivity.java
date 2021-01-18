package com.example.furnico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.furnico.retrofit.model.Customer;
import com.example.furnico.retrofit.model.ResponseString;
import com.example.furnico.retrofit.network.IPostApi;
import com.example.furnico.retrofit.networkmanager.RetrofitBuilderUserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import android.util.Patterns;
public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Button btnLogin = findViewById(R.id.bt_activity_login_login);
        Button btnRegister = findViewById(R.id.bt_activity_login_register);
        btnLogin.setOnClickListener(v -> {

            String email = ((EditText) findViewById(R.id.et_activity_login_email)).getText().toString();
            String pass = ((EditText) findViewById(R.id.et_activity_login_password)).getText().toString();

            if (email.length() == 0 || pass.length() == 0 ) {
                Toast.makeText(LoginActivity.this, "Length should be greater than 0", Toast.LENGTH_SHORT).show();
            } else {
                Customer customer = new Customer();
                customer.setEmail(email);
                customer.setPassword(pass);
                Retrofit retrofit = RetrofitBuilderUserService.getInstance();
                IPostApi iPostApi = retrofit.create(IPostApi.class);
                Call<ResponseString> responses = iPostApi.loginPost(customer);
                Callback<ResponseString> userCallback = new Callback<ResponseString>() {
                    @Override
                    public void onResponse(Call<ResponseString> call, Response<ResponseString> response) {
                        Log.d("Login", "Reponse" + response.body());
                        if (response.body().getValue().equals("failure"))
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        else {
                            SharedPreferences sharedPreferences = getSharedPreferences("com.example.androidTrainingPA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userid", response.body().getValue());
                            editor.apply();
                            editor.commit();
                            int isCart = getIntent().getIntExtra("cartLogin", 0);
                            if (isCart == 1) {
//                                Intent intent = new Intent(LoginActivity.this,C.class);
//                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }

                    }
                    @Override
                    public void onFailure(Call<ResponseString> call, Throwable t) {
                        t.printStackTrace();
                    }
                };
                responses.enqueue(userCallback);


            }
        });

        btnRegister.setOnClickListener(v1 -> {
            Log.d("Register","Clicked");
            Intent intent = new Intent(this,RegistrationActivity.class);
            startActivity(intent);
        });
    }
}


