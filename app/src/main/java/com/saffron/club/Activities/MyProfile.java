package com.saffron.club.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.saffron.club.Models.UserModel;
import com.saffron.club.NetworkResponses.ChangePasswordResponse;
import com.saffron.club.NetworkResponses.SaveAddressResponse;
import com.saffron.club.NetworkResponses.SaveUserResponse;
import com.saffron.club.NetworkResponses.UploadProfilePictureReponse;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;
import com.saffron.club.Utils.CompressImage;
import com.saffron.club.Utils.Glide4Engine;
import com.saffron.club.Utils.SharedPrefs;
import com.saffron.club.Utils.UserClient;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyProfile extends AppCompatActivity {

    EditText name, email, cell1, cell2, cell3;
    EditText password, confirmPassword;
    EditText address1, address2, address3;
    Button saveGeneral, savePassword, saveAddress;
    RelativeLayout wholeLayout;
    CircleImageView profilePicture;
    private List<Uri> mSelected = new ArrayList<>();
    private List<String> imageUrl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        getPermissions();
        this.setTitle("My profile");
        findViewByIds();
        onClicks();


    }


    private void findViewByIds() {
        profilePicture = findViewById(R.id.profilePicture);
        wholeLayout = findViewById(R.id.wholeLayout);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        cell1 = findViewById(R.id.cell1);
        cell2 = findViewById(R.id.cell2);
        cell3 = findViewById(R.id.cell3);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        address1 = findViewById(R.id.address1);
        address2 = findViewById(R.id.address2);
        address3 = findViewById(R.id.address3);

        saveAddress = findViewById(R.id.saveAddress);
        saveGeneral = findViewById(R.id.saveGeneral);
        savePassword = findViewById(R.id.savePassword);
        setPrefilledData();

    }

    private void setPrefilledData() {
        name.setText(SharedPrefs.getUserModel().getName());
        email.setText(SharedPrefs.getUserModel().getEmail());
        cell1.setText(SharedPrefs.getUserModel().getCell1());
        cell2.setText(SharedPrefs.getUserModel().getCell2());
        cell3.setText(SharedPrefs.getUserModel().getCell3());
        address1.setText(SharedPrefs.getUserModel().getAddress1());
        address2.setText(SharedPrefs.getUserModel().getAddress2());
        address3.setText(SharedPrefs.getUserModel().getAddress3());
        if(SharedPrefs.getUserModel().getImage()!=null) {
            Glide.with(MyProfile.this).load(AppConfig.BASE_URL_Image + SharedPrefs.getUserModel().getImage()).into(profilePicture);
        }
    }

    private void onClicks() {

        saveGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() == 0) {
                    name.setError("Enter Name");
                } else if (cell1.getText().length() == 0) {
                    cell1.setError("Enter Number");
                } else {
                    updateGeneralData();
                }
            }
        });
        saveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address1.getText().length() == 0) {
                    name.setError("Enter address");
                } else {
                    saveAddressProfile();
                }
            }
        });
        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else if (confirmPassword.getText().length() == 0) {
                    confirmPassword.setError("Enter confirm Password");
                } else if (password.getText().length() < 8) {
                    password.setError("Password should be atleast 8 characters");
                } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    confirmPassword.setError("Password do not match");
                } else {
                    updatePassword();
                }
            }
        });
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMatisse();
            }
        });
    }


    private void updateGeneralData() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<SaveUserResponse> call = getResponse.saveGeneral(
                SharedPrefs.getToken(),
                name.getText().toString(),
                cell1.getText().toString(),
                cell2.getText().toString(),
                cell3.getText().toString()

        );
        call.enqueue(new Callback<SaveUserResponse>() {
            @Override
            public void onResponse(Call<SaveUserResponse> call, Response<SaveUserResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    SaveUserResponse object = response.body();
                    if (object != null) {
                        UserModel user = object.getUserModel();
                        if (user != null) {
                            SharedPrefs.setUserModel(user);
                            setPrefilledData();
                            CommonUtils.showToast("Profile updated");

                        }
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<SaveUserResponse> call, Throwable t) {
                t.printStackTrace();
                CommonUtils.showToast(t.getMessage());
                wholeLayout.setVisibility(View.GONE);
            }
        });

    }

    private void initMatisse() {
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(23);
    }


    private void updatePassword() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ChangePasswordResponse> call = getResponse.changePassword(
                SharedPrefs.getToken(),
                password.getText().toString(),
                confirmPassword.getText().toString()


        );
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    ChangePasswordResponse object = response.body();
                    if (object != null) {
                        UserModel user = object.getUserModel();
                        if (user != null) {
                            SharedPrefs.setUserModel(user);
                            setPrefilledData();
                            CommonUtils.showToast("Password Successfully Changed");
                        }
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                t.printStackTrace();
                CommonUtils.showToast(t.getMessage());
                wholeLayout.setVisibility(View.GONE);
            }
        });

    }

    private void saveAddressProfile() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<SaveAddressResponse> call = getResponse.saveAddress(
                SharedPrefs.getToken(),
                address1.getText().toString(),
                address2.getText().toString(),
                address3.getText().toString());

        call.enqueue(new Callback<SaveAddressResponse>() {
            @Override
            public void onResponse(Call<SaveAddressResponse> call, Response<SaveAddressResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    SaveAddressResponse object = response.body();
                    if (object != null) {
                        UserModel user = object.getUserModel();
                        if (user != null) {
                            SharedPrefs.setUserModel(user);
                            setPrefilledData();
                            CommonUtils.showToast("Address Updated Successfully");
                        }
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<SaveAddressResponse> call, Throwable t) {
                t.printStackTrace();
                CommonUtils.showToast(t.getMessage());
                wholeLayout.setVisibility(View.GONE);
            }
        });

    }

    private void uploadFile() {
        // create upload service client
        wholeLayout.setVisibility(View.VISIBLE);
        File file = new File(imageUrl.get(0));

        UserClient service = AppConfig.getRetrofit().create(UserClient.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("profile", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        // finally, execute the request
        Call<UploadProfilePictureReponse> call = service.uploadPicture(SharedPrefs.getToken(), fileToUpload, filename);
        call.enqueue(new Callback<UploadProfilePictureReponse>() {
            @Override
            public void onResponse(Call<UploadProfilePictureReponse> call,
                                   Response<UploadProfilePictureReponse> response) {
                Log.v("Upload", "success");
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    UploadProfilePictureReponse object = response.body();
                    if (object != null) {
                        UserModel user = object.getUserModel();
                        if (user != null) {
                            SharedPrefs.setUserModel(user);
                            setPrefilledData();
                            CommonUtils.showToast("Picture Uploaded");
                        }
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<UploadProfilePictureReponse> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                wholeLayout.setVisibility(View.GONE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 23 && data != null) {

            mSelected = Matisse.obtainResult(data);
            Glide.with(MyProfile.this).load(mSelected.get(0)).into(profilePicture);
            CompressImage compressImage = new CompressImage(MyProfile.this);
            imageUrl.add(compressImage.compressImage("" + mSelected.get(0)));
            uploadFile();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,

        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


}
