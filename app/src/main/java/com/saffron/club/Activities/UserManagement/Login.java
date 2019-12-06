package com.saffron.club.Activities.UserManagement;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.navigation.NavigationView;
import com.saffron.club.Activities.MainActivity;
import com.saffron.club.Models.FacebookProfileModel;
import com.saffron.club.Models.UserModel;
import com.saffron.club.NetworkResponses.LoginResponse;
import com.saffron.club.NetworkResponses.SignupResponse;
import com.saffron.club.NetworkResponses.UserDetailsResponse;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;
import com.saffron.club.Utils.SharedPrefs;
import com.saffron.club.Utils.UserClient;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView signup, forgot;
    ImageView google, facebook;
    Button login;

    EditText password, email;

    RelativeLayout wholeLayout;
    LoginButton login_button;
    public static FacebookProfileModel profile;
    GoogleApiClient apiClient;
    SignInButton google1;
    public static GoogleSignInAccount account;
    private CallbackManager callbackManager;
    boolean social = false;
    boolean fb = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        apiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        callbackManager = CallbackManager.Factory.create();

        login_button = findViewById(R.id.login_button);
        signup = findViewById(R.id.signup);
        forgot = findViewById(R.id.forgot);
        google = findViewById(R.id.google);
        facebook = findViewById(R.id.facebook);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        wholeLayout = findViewById(R.id.wholeLayout);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = null;
                profile = null;
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getUserModelFromDB("dfdsfsd");
                if (email.getText().length() == 0) {
                    email.setError("Enter Email");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else {
                    loginUser(email.getText().toString(), password.getText().toString());
                }
            }
        });
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                google1.performClick();
                Intent i = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                startActivityForResult(i, 100);
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_button.performClick();
            }
        });
        login_button.setReadPermissions(Arrays.asList("public_profile", "email"));

        login_button.registerCallback(callbackManager, callback);
    }

    FacebookCallback callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
//                        profile = Profile.getCurrentProfile();

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());

                            // Application code
                            try {
                                Login.account = null;
                                fb = true;
                                String email = object.getString("email");
                                String firstName = object.getString("name").split(" ")[0];
                                String lastName = object.getString("name").split(" ")[1];
                                String id = object.getString("id");
                                profile = new FacebookProfileModel(id, firstName, lastName, email);
                                social = true;

                                loginUser(email, id);



                            } catch (Exception e) {
                                CommonUtils.showToast(e.getMessage());
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender");
            request.setParameters(parameters);
            request.executeAsync();


            // App code
        }

        @Override
        public void onCancel() {
            // App code
        }

        @Override
        public void onError(FacebookException exception) {
            // App code
        }
    };

    private void registerUser(String namee, String emaill, String passwordd, String cpasswordd) {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<SignupResponse> call = getResponse.signUp(
                namee,
                emaill,
                passwordd,
                cpasswordd
        );
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.code() == 401) {
                    wholeLayout.setVisibility(View.GONE);
                    CommonUtils.showToast("Unauthorized");
                } else if (response.code() == 500) {
                    wholeLayout.setVisibility(View.GONE);
                    CommonUtils.showToast("Something went wrong");
                } else if (response.code() == 200) {
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

    private void loginUser(String email, String password) {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<LoginResponse> call = getResponse.loginUser(email, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 401) {

                    if (social) {
                        if (fb) {
                            registerUser(profile.getFirstName() + " " + profile.getLastName(),
                                    profile.getEmail(), profile.getId(), profile.getId()
                            );
                        } else {
                            registerUser(account.getDisplayName(),
                                    account.getEmail(), account.getId(), account.getId()
                            );
                        }
                    } else {
                        wholeLayout.setVisibility(View.GONE);
                        CommonUtils.showToast("Either username or password is not correct");
                    }
                } else {
                    wholeLayout.setVisibility(View.GONE);

                    LoginManager.getInstance().logOut();
                    if (response.isSuccessful()) {
                        LoginResponse object = response.body();
                        if (object != null) {
                            if(object.getMeta().getMessage().equalsIgnoreCase("Password Not Matched")){
                                CommonUtils.showToast("Password do not match");
                            }else if(object.getMeta().getMessage().equalsIgnoreCase("Account Not Found")){
                                if(social){
                                    registerUser(account.getDisplayName(),
                                            account.getEmail(), account.getId(), account.getId()
                                    );
                                }
                            }
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
            public void onFailure(Call<LoginResponse> call, Throwable t) {
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
                                CommonUtils.showToast("Login Successful");

                                wholeLayout.setVisibility(View.GONE);
                                SharedPrefs.setUserModel(user);
                                startActivity(new Intent(Login.this, MainActivity.class));
                                finish();

                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
                wholeLayout.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(googleSignInResult);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void handleResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            account = googleSignInResult.getSignInAccount();
            Login.profile = null;
            String email = account.getEmail();
            String pass = account.getId();
            social = true;
            loginUser(email, pass);


            Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {

                }
            });

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
