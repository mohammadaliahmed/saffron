package com.saffron.club.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.saffron.club.Adapters.OrderListAdapter;
import com.saffron.club.Adapters.ProductListAdapter;
import com.saffron.club.Models.OrderModel;
import com.saffron.club.Models.Product;
import com.saffron.club.NetworkResponses.ListOfOrdersResponse;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;
import com.saffron.club.Utils.SharedPrefs;
import com.saffron.club.Utils.UserClient;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyOrders extends AppCompatActivity {

    RecyclerView recyclerView;
    OrderListAdapter adapter;
    List<OrderModel> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
//        this.setTitle("");

        recyclerView = findViewById(R.id.recyclerView);
        initOrders();


    }

    private void initOrders() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new OrderListAdapter(this, itemList);
        recyclerView.setAdapter(adapter);
        getMyOrdersDataFromDB();
    }

    private void getMyOrdersDataFromDB() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ListOfOrdersResponse> call = getResponse.getOrders(
                SharedPrefs.getToken()
        );
        call.enqueue(new Callback<ListOfOrdersResponse>() {
            @Override
            public void onResponse(Call<ListOfOrdersResponse> call, Response<ListOfOrdersResponse> response) {
                if (response.isSuccessful()) {
                    ListOfOrdersResponse object = response.body();
                    if (object != null && object.getOrders() != null) {
                        if (object.getOrders().size() > 0) {
                            for (OrderModel model : object.getOrders()) {
//                                if (model.getuId().equalsIgnoreCase("" + SharedPrefs.getUserModel().getId())) {
                                itemList.add(model);
                            }
//                            }

                            adapter.setItemList(itemList);
                        } else {
                            CommonUtils.showToast("No products Data");
                        }
                    } else {
                        CommonUtils.showToast("There is some error");
                    }
                }

            }

            @Override
            public void onFailure(Call<ListOfOrdersResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });
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
