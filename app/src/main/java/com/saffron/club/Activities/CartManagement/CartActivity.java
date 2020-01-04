package com.saffron.club.Activities.CartManagement;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.saffron.club.Activities.MainActivity;
import com.saffron.club.Activities.MapsActivity;
import com.saffron.club.Activities.StripeCheckOut;
import com.saffron.club.Models.BookingModel;
import com.saffron.club.Models.MenuModel;
import com.saffron.club.Models.PaypalResponse;
import com.saffron.club.NetworkResponses.ConfirmBookingResponse;
import com.saffron.club.NetworkResponses.PlaceOrderResponse;
import com.saffron.club.NetworkResponses.RemoveMenuResponse;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;
import com.saffron.club.Utils.SharedPrefs;
import com.saffron.club.Utils.UserClient;
import com.stripe.android.model.Card;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
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
    TextView chooseLocation, distance, cost;
    RadioButton homeDelivery, pickUp;
    LinearLayout deliveryTypeLayout;


    //paypal
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId("AWwiDoy5r3PPGaA4GEnGuJX3_iNsLiF_zYtZKkBrdNTTCvGybzCURusWuq-Q-8j_fLE454JT70Jp4yUE");

    private static final int REQUEST_CODE_PAYMENT = 1;
    PayPalPayment thingToBuy;

    private double lat, lon;
    private String returnString;
    private double distn = 0;

    RelativeLayout wholeLayout;
    LinearLayout deliverySection;
    String deliveryType;
    private boolean toAddDistance;
    private String paymentId;


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
        homeDelivery = findViewById(R.id.homeDelivery);
        deliveryTypeLayout = findViewById(R.id.deliveryTypeLayout);
        pickUp = findViewById(R.id.pickUp);
        placeOrder = findViewById(R.id.placeOrder);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerTable = findViewById(R.id.recyclerTable);
        totalAmount = findViewById(R.id.totalAmount);
        chooseLocation = findViewById(R.id.chooseLocation);
        wholeLayout = findViewById(R.id.wholeLayout);
        cost = findViewById(R.id.cost);
        deliverySection = findViewById(R.id.deliverySection);
        distance = findViewById(R.id.distance);


        deliveryType = "Pick Up";
        deliverySection.setVisibility(View.GONE);


        homeDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    deliveryType = "Home Delivery";
                    deliverySection.setVisibility(View.VISIBLE);
                }
                toAddDistance = true;

            }
        });
        pickUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    deliverySection.setVisibility(View.GONE);
                    deliveryType = "Pick Up";
                }
                toAddDistance = false;

            }
        });

        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this, MapsActivity.class);
                startActivityForResult(i, 1);
            }
        });

        getTokenFromServer();
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                String title = "Saffron CLub";
                                BigDecimal Amount = BigDecimal.valueOf(total);
                                startPurchasePayPal(title, Amount);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                Intent intent = new Intent(CartActivity.this, StripeCheckOut.class);
                                startActivity(intent);
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                builder.setMessage("Your Payment type?").setPositiveButton("Paypal", dialogClickListener)
                        .setNegativeButton("Stripe", dialogClickListener).show();




//                DropInRequest dropInRequest = new DropInRequest()
//                        .clientToken(clientToken);
//                startActivityForResult(dropInRequest.getIntent(CartActivity.this), REQUEST_CODE);
//                placeOrderNow();

              /*  String title = "Saffron CLub";
                BigDecimal Amount = BigDecimal.valueOf(total);
                startPurchasePayPal(title, Amount);*/


//                if (returnString == null) {
//                    CommonUtils.showToast("Please select delivery location");
//                } else {
//
//                    placeOrderNow();
//                }


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

    private void placeOrderNow() {
        wholeLayout.setVisibility(View.VISIBLE);

        HashMap<String, Object> finalMap = new HashMap<>();

        finalMap.put("table", tableList);
        finalMap.put("menu", itemList);
        finalMap.put("totalPrice", total);
        finalMap.put("time", System.currentTimeMillis());
        finalMap.put("deliveryLat", lat);
        finalMap.put("deliveryLon", lon);
        finalMap.put("distance", distn);
        finalMap.put("cost", distn);


        Gson gson = new Gson();
        String abc = gson.toJson(finalMap);
        JsonObject map = new JsonObject();

        map.addProperty("order", abc);

        String finalUrl = "";
        String abadac = String.format("%.2f", total);

        finalUrl = finalUrl + "/web/api/placeorder?cooking_time=30&cost=" + (int) distn + "&totalPrice=" + abadac;
        int count = 0;

        for (BookingModel model : tableList) {
            String tab = "";
            tab = tab + "&t_id[" + count + "]=" + model.gettId();
            tab = tab + "&persons[" + count + "]=" + model.getPersons();
            tab = tab + "&time[" + count + "]=" + model.getTime();
            tab = tab + "&date[" + count + "]=" + model.getDate();

            count++;
            finalUrl = finalUrl + tab;
        }
        count = 0;
        int extraCount = 0;
        for (MenuModel model : itemList) {
            String menu = "";
            menu = menu + "&product[" + count + "]=" + model.getProduct().getId();
            if (model.getVariation() != null) {
                menu = menu + "&extra[" + count + "]=" + model.getVariation().getId();
                menu = menu + "&price[" + count + "]=" + model.getVariation().getPrice();

            } else {
                menu = menu + "&extra[" + count + "]=" + 0;
                menu = menu + "&price[" + count + "]=" + model.getProduct().getPrice();

            }
            menu = menu + "&quantity[" + count + "]=" + model.getQuantity();
            double subtotal = 0;
            if (model.getVariation() != null) {
                subtotal = Double.parseDouble(model.getVariation().getPrice()) * model.getQuantity();
                menu = menu + "&varid[" + count + "]=" + model.getVariation().getId();

            } else {
                subtotal = Double.parseDouble(model.getProduct().getPrice()) * model.getQuantity();
            }
            menu = menu + "&subtotal[" + count + "]=" + subtotal;

            count++;
            finalUrl = finalUrl + menu;
        }
        int typee = 0;
        if (deliveryType.equalsIgnoreCase("Pick up")) {
            typee = 1;
        } else if (deliveryType.equalsIgnoreCase("Home Delivery")) {
            typee = 2;

        }
        finalUrl = finalUrl + "&order_type=" + typee;
        finalUrl = finalUrl + "&lang=" + lon;
        finalUrl = finalUrl + "&lat=" + lat;
        finalUrl = finalUrl + "&drop_location=" + CommonUtils.getFullAddress(CartActivity.this, lat, lon);
        finalUrl = finalUrl + "&payment_method=Paypal";
        finalUrl = finalUrl + "&payment_status=1";
        finalUrl = finalUrl + "&transaction_id=" + paymentId;

        count = 0;


        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<PlaceOrderResponse> call = getResponse.placeOrder(
                SharedPrefs.getToken(),
                finalUrl
        );
        call.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(Call<PlaceOrderResponse> call, Response<PlaceOrderResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body().getMeta().getMessage().equalsIgnoreCase("Order Placed ")) {
                        CommonUtils.showToast("Order Placed Succesffuly");
                        SharedPrefs.clearCartMenuIds();
                        SharedPrefs.clearTableIds();
                        Intent i = new Intent(CartActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    } else {
                        CommonUtils.showToast(response.body().getMeta().getMessage());
                    }
                } else {
                    CommonUtils.showToast("There is some error");
                }
            }

            @Override
            public void onFailure(Call<PlaceOrderResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
                wholeLayout.setVisibility(View.GONE);
            }
        });
    }
//

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
                if (map != null) {
                    map.remove(Integer.parseInt(productt));
                    SharedPrefs.setCartMenuIds(map);
                    if (itemList.size() == 0) {
                        SharedPrefs.clearCartMenuIds();
                    }
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
                        SharedPrefs.clearTableIds();
                        if (tableList.size() > 0) {
                            toAddDistance = false;
                            deliverySection.setVisibility(View.GONE);
                            deliveryTypeLayout.setVisibility(View.GONE);
                        } else {
                            deliverySection.setVisibility(View.GONE);
                            deliveryTypeLayout.setVisibility(View.VISIBLE);
                            toAddDistance = true;

                        }
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
                        if (tableList.size() > 0) {
                            deliverySection.setVisibility(View.GONE);
                            deliveryTypeLayout.setVisibility(View.GONE);
                        } else {
                            deliverySection.setVisibility(View.GONE);
                            deliveryTypeLayout.setVisibility(View.VISIBLE);


                        }
                        for (MenuModel menuModel : confirmBookingResponse.getConfirmBookingModel().getMenu()) {
                            if (menuModel.getProduct() != null) {
                                menuModel.setQuantity(1);
                                boolean found = false;
//                                for (MenuModel menuModel1 : itemList) {
//                                    if (menuModel.getProduct().getId() == menuModel1.getProduct().getId()) {
//                                        found = true;
//                                    }
//                                }
//                                if (!found) {
                                    itemList.add(menuModel);
//                                }
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
            if(Integer.parseInt(menuModel.getExtra())==0) {
                total = total + (menuModel.getQuantity() * Double.parseDouble(menuModel.getProduct().getPrice()));
            }else{
                total = total + (menuModel.getQuantity() * Double.parseDouble(menuModel.getVariation().getPrice()));

            }
        }
        total = total + distn;
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

    private void postNonceToServer(String nonce) {
        UserClient getResponse = AppConfig.getTokenUrl().create(UserClient.class);
        Call<ResponseBody> call = getResponse.braintreeCheckout(
                nonce
        );
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

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
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                if (deliveryType.equalsIgnoreCase("home delivery")) {
                    returnString = data.getStringExtra("address");
                    lat = data.getDoubleExtra("lat", SharedPrefs.getUserModel().getLat());
                    lon = data.getDoubleExtra("lon", SharedPrefs.getUserModel().getLon());

                    chooseLocation.setText("Delivery: " + returnString);

                    distn = CommonUtils.distance(lat, lon, -34.877180, 138.601850);
                    distance.setText("Distance: " + String.format("%.2f", distn) + "m");
                    cost.setText("Cost: $" + String.format("%.2f", distn * 1));
                    calculateTotal();
                }


            }
        }
        if (requestCode == REQUEST_CODE)

        {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                if (result != null) {
                    String paymentNonce = result.getPaymentMethodNonce().getNonce();
                    if (paymentNonce != null) {
                        postNonceToServer(paymentNonce);
                    }
                }
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
        if (requestCode == REQUEST_CODE_PAYMENT)

        {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject()
                                .toString(4));
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = parser.parse(confirm.toJSONObject().toString());
                        Gson gson = new Gson();
                        PaypalResponse object = gson.fromJson(mJson, PaypalResponse.class);
//                        CommonUtils.showToast(object.getResponse().getState());
                        if (object.getResponse().getState().equalsIgnoreCase("approved")) {
                            CommonUtils.showToast("Payment successfull");
                            paymentId = object.getResponse().getId();
                            placeOrderNow();
//                            finish();
                        }
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