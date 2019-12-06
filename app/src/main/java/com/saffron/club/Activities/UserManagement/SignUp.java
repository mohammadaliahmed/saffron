package com.saffron.club.Activities.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.saffron.club.Activities.MainActivity;
import com.saffron.club.Models.UserModel;
import com.saffron.club.NetworkResponses.LoginResponse;
import com.saffron.club.NetworkResponses.SignupResponse;
import com.saffron.club.NetworkResponses.UserDetailsResponse;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;
import com.saffron.club.Utils.SharedPrefs;
import com.saffron.club.Utils.UserClient;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {
    Button register;

    EditText name, cpassword, password, email;
    TextView login;

    RelativeLayout wholeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        cpassword = findViewById(R.id.cpassword);
        email = findViewById(R.id.email);
        wholeLayout = findViewById(R.id.wholeLayout);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, Login.class));
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else if (email.getText().length() == 0) {
                    email.setError("Enter Email");
                }
                if (password.getText().length() == 0) {
                    password.setError("Enter Email");
                } else if (cpassword.getText().length() == 0) {
                    cpassword.setError("Enter password");
                } else if (!password.getText().toString().equalsIgnoreCase(cpassword.getText().toString())) {
                    cpassword.setError("Passwords do not match");
                } else {

                    registerUser();
                }
            }
        });


    }

    private void registerUser() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<SignupResponse> call = getResponse.signUp(
                name.getText().toString(),
                email.getText().toString(),
                password.getText().toString(),
                cpassword.getText().toString()
        );
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.code() == 401) {
                    wholeLayout.setVisibility(View.GONE);
                    CommonUtils.showToast("Something went wrong");
                    return;
                } else {
                    if (response.isSuccessful()) {
                        SignupResponse object = response.body();
                        if (object != null) {
                            if (object.getSuccess() != null) {
                                String token = object.getSuccess().getToken();
                                SharedPrefs.setToken("Bearer " + token);
                                getUserModelFromDB(SharedPrefs.getToken());
                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
                wholeLayout.setVisibility(View.GONE);
            }
        });

    }

    private void getUserModelFromDB(String token) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<UserDetailsResponse> call = getResponse.userDetails(
                token
        );
        call.enqueue(new Callback<UserDetailsResponse>() {
            @Override
            public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                if (response.isSuccessful()) {
                    UserDetailsResponse object = response.body();
                    if (object != null) {
                        UserModel user = object.getUser();
                        if (user != null) {
                            if (user.getName() != null) {
                                CommonUtils.showToast("Signup Successful");
                                SharedPrefs.setUserModel(user);
                                startActivity(new Intent(SignUp.this, MainActivity.class));
                                finish();

                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });

    }


}
