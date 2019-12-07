package com.saffron.club.Activities.CartManagement;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.saffron.club.Activities.MainActivity;
import com.saffron.club.Models.BookingModel;
import com.saffron.club.Models.MenuModel;
import com.saffron.club.NetworkResponses.ConfirmBookingResponse;
import com.saffron.club.NetworkResponses.RemoveMenuResponse;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;
import com.saffron.club.Utils.SharedPrefs;
import com.saffron.club.Utils.UserClient;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 25;
    TextView placeOrder;

    RecyclerView recyclerView, recyclerTable;

    CartMenuAdapter adapter;
    CartTableAdapter tableAdapter;
    private List<MenuModel> itemList = new ArrayList<>();
    private ConfirmBookingResponse confirmBookingResponse;
    private List<BookingModel> tableList = new ArrayList<>();
    TextView totalAmount;
    private String clientToken;
    private double total;
    private String totall;

    private EditText mNumber, mMonth, mYear, mCVC, mName;
    private boolean mLenNumber = false, mLenMonth = false, mLenYear = false, mLenCVC = false, mLenName = false;

    //paypal
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId("AWwiDoy5r3PPGaA4GEnGuJX3_iNsLiF_zYtZKkBrdNTTCvGybzCURusWuq-Q-8j_fLE454JT70Jp4yUE");

    private static final int REQUEST_CODE_PAYMENT = 1;
    PayPalPayment thingToBuy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Cart");
        placeOrder = findViewById(R.id.placeOrder);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerTable = findViewById(R.id.recyclerTable);
        totalAmount = findViewById(R.id.totalAmount);

        getTokenFromServer();
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //card payment activity start here

//                Intent send = new Intent(CartActivity.this, Pay.class);
//                send.putExtra("amount",totall);
//                startActivity(send);


                //Paypal Payment activity start here
                String title = "Order1";
                BigDecimal Amount = BigDecimal.valueOf(100);
                startPurchasePayPal(title, Amount);




                //your previous work



//                showBottomDialog();
//                charge.save();

//                DropInRequest dropInRequest = new DropInRequest()
//                        .clientToken(clientToken);
//                startActivityForResult(dropInRequest.getIntent(CartActivity.this), REQUEST_CODE);
//                DropInRequest dropInRequest = new DropInRequest()
//                        .clientToken(clientToken);
//                startActivityForResult(dropInRequest.getIntent(CartActivity.this), REQUEST_CODE);


            }
        });
        adapter = new CartMenuAdapter(this, itemList, new CartMenuAdapter.CartCallbacks() {
            @Override
            public void onDeleteMenu(MenuModel menu, int position) {

                showDeleteAlert(menu.getId(), position, menu.getProductt());

            }

            @Override
            public void onIncrease(int value, int position) {
                MenuModel abc = itemList.get(position);
                abc.setQuantity(value);
                itemList.set(position, abc);
                calculateTotal();

            }

            @Override
            public void onDecrease(int value, int position) {
                MenuModel abc = itemList.get(position);
                abc.setQuantity(value);
                itemList.set(position, abc);
                calculateTotal();
            }
        });


        tableAdapter = new CartTableAdapter(this, tableList, new CartTableAdapter.CartTableCallbacks() {
            @Override
            public void onDeleteTable(BookingModel table, int position) {
                showTableDeleteAlert(table.getId(), position);
            }
        });
        recyclerTable.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerTable.setAdapter(tableAdapter);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        getCartDataFromServer();

    }

    private void showBottomDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(CartActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_card_design, null);

        dialog.setContentView(layout);
        TextView payAmount = layout.findViewById(R.id.payAmount);
        payAmount.setText("Pay $" + totall + " using your card");

        mNumber = layout.findViewById(R.id.num);
        mMonth = layout.findViewById(R.id.month);
        mYear = layout.findViewById(R.id.year);
        mCVC = layout.findViewById(R.id.cvc);
        mName = layout.findViewById(R.id.card_name);
        Button pay = layout.findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputData())
                    cardDetails(dialog);
            }
        });
        mNumber.addTextChangedListener(new FourDigitCardFormatWatcher());


        dialog.show();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private void cardDetails(BottomSheetDialog dialog) {

        String num = mNumber.getText().toString().replaceAll("-", "");
        Card.Builder build = new Card.Builder(
                num,
                Integer.valueOf(mMonth.getText().toString()),
                Integer.valueOf(mYear.getText().toString()),
                mCVC.getText().toString()
        );
        build.name(mName.getText().toString());
        //You can add many more fields like address, pin, etc
        Card card = build.build();

        if (!card.validateCVC()) {
            mCVC.setError("Invalid Card CVC");
            return;
        }
        if (!card.validateExpMonth()) {
            mMonth.setError("Invalid Card Month");
            return;
        }
        if (!card.validateNumber()) {
            mNumber.setError("Invalid Card Number");
            return;
        }
        if (!card.validateExpiryDate()) {
            mYear.setError("Invalid Card Year");
            return;
        }

        if (card.validateCard()) { //if card is valid then stripe payment is called
            if (num.equalsIgnoreCase("4242424242424242")) {
                CommonUtils.showToast("Payment successfull");
                dialog.dismiss();

            }
//            stripeCall(card);
        } else {
            Toast.makeText(CartActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validateInputData() {
        if (mNumber.getText().toString().length() == 19) {
            mLenNumber = true;
        } else
            mNumber.setError("Invalid Card Number");

        if (mMonth.getText().toString().length() > 0) {
            mLenMonth = true;
        } else
            mMonth.setError("Invalid Month");

        if (mYear.getText().toString().length() == 2) {
            mLenYear = true;
        } else
            mYear.setError("Invalid Year");

        if (mCVC.getText().toString().length() == 3) {
            mLenCVC = true;
        } else
            mCVC.setError("Invalid CVC");

        if (mName.getText().toString().length() > 0) {
            mLenName = true;
        } else
            mName.setError("Invalid Name");

        if (mLenCVC && mLenYear && mLenMonth && mLenNumber) {
            return true;
        }
        return false;
    }

    private void showDeleteAlert(final int idd, final int positionn, final String productt) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to delete this item from cart ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itemList.remove(positionn);
                adapter.setItemList(itemList);
                HashMap<Integer, Integer> map = SharedPrefs.getCartMenuIds();
                map.remove(Integer.parseInt(productt));
                SharedPrefs.setCartMenuIds(map);
                if (itemList.size() == 0) {
                    SharedPrefs.clearCartMenuIds();
                }
                calculateTotal();
                removeMenuApi(idd);

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showTableDeleteAlert(final int idd, final int positionn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to remove this table?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tableList.remove(positionn);
                tableAdapter.setItemList(tableList);
//                if (tableList.size() == 0) {
//                    SharedPrefs.clearCartMenuIds();
//                }
                removeTableApi(idd);

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void removeMenuApi(int id) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<RemoveMenuResponse> call = getResponse.removeMenu(
                SharedPrefs.getToken(),
                "" + id
        );
        call.enqueue(new Callback<RemoveMenuResponse>() {
            @Override
            public void onResponse(Call<RemoveMenuResponse> call, Response<RemoveMenuResponse> response) {
                if (response.code() == 200) {
                    RemoveMenuResponse abc = response.body();
                    if (abc.getMeta().getMessage().equalsIgnoreCase("Successfully Removed")) {
                        CommonUtils.showToast("Item Removed");
                    }
                }
            }

            @Override
            public void onFailure(Call<RemoveMenuResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });


    }

    private void removeTableApi(int id) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<RemoveMenuResponse> call = getResponse.removeTable(
                SharedPrefs.getToken(),
                "" + id
        );
        call.enqueue(new Callback<RemoveMenuResponse>() {
            @Override
            public void onResponse(Call<RemoveMenuResponse> call, Response<RemoveMenuResponse> response) {
                if (response.code() == 200) {
                    RemoveMenuResponse abc = response.body();
                    if (abc.getMeta().getMessage().equalsIgnoreCase("Successfully Removed")) {
                        CommonUtils.showToast("Item Removed");
                    }
                }
            }

            @Override
            public void onFailure(Call<RemoveMenuResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });


    }

    private void getCartDataFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ConfirmBookingResponse> call = getResponse.confirmBooking(
                SharedPrefs.getToken()
        );
        call.enqueue(new Callback<ConfirmBookingResponse>() {
            @Override
            public void onResponse(Call<ConfirmBookingResponse> call, Response<ConfirmBookingResponse> response) {
                if (response.code() == 200) {

                    confirmBookingResponse = response.body();
                    if (confirmBookingResponse != null) {
                        itemList.clear();
                        tableList.clear();
                        tableList = confirmBookingResponse.getConfirmBookingModel().getBookings();
                        for (MenuModel menuModel : confirmBookingResponse.getConfirmBookingModel().getMenu()) {
                            if (menuModel.getProduct() != null) {
                                menuModel.setQuantity(1);
                                itemList.add(menuModel);
                            }
                        }
                        if (itemList.size() > 0) {
                            calculateTotal();
                        }

                        adapter.setItemList(itemList);
                        tableAdapter.setItemList(tableList);
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ConfirmBookingResponse> call, Throwable t) {

            }
        });

    }

    private void calculateTotal() {
        total = 0;
        for (MenuModel menuModel : itemList) {
            total = total + (menuModel.getQuantity() * Double.parseDouble(menuModel.getProduct().getPrice()));
        }
        totall = String.format("%.2f", total);
        totalAmount.setText("$" + totall);
    }

    private void getTokenFromServer() {
        UserClient getResponse = AppConfig.getTokenUrl().create(UserClient.class);
        Call<ResponseBody> call = getResponse.geTokken(
        );
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    clientToken = response.body().string();
//                DropInRequest dropInRequest = new DropInRequest()
//                        .clientToken(clientToken);
//                startActivityForResult(dropInRequest.getIntent(CartActivity.this), REQUEST_CODE);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


    private void startPurchasePayPal(String PurchaseTitle, BigDecimal TotalAmount) {


        thingToBuy = new PayPalPayment(TotalAmount, "USD",
                PurchaseTitle, PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(CartActivity.this,
                PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject()
                                .toString(4));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Your code here on Success case

                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                System.out
                        .println("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
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

}