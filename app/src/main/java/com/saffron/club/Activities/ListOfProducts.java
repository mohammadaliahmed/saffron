package com.saffron.club.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.saffron.club.Activities.Callbacks.AddToCartCallback;
import com.saffron.club.Activities.CartManagement.CartActivity;
import com.saffron.club.Adapters.ProductListAdapter;
import com.saffron.club.Models.Product;
import com.saffron.club.NetworkResponses.AddToCartResponse;
import com.saffron.club.NetworkResponses.ProductResponse;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;
import com.saffron.club.Utils.SharedPrefs;
import com.saffron.club.Utils.UserClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListOfProducts extends AppCompatActivity {

    RecyclerView recyclerView;
    ProductListAdapter adapter;
    List<Product> itemList = new ArrayList<>();
    EditText search;
    int categoryId;
    private ArrayList productIdList = new ArrayList();
    RelativeLayout wholeLayout;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        categoryId = getIntent().getIntExtra("categoryId", 1);
        categoryName = getIntent().getStringExtra("categoryName");
        this.setTitle(categoryName);

        recyclerView = findViewById(R.id.recyclerView);
        wholeLayout = findViewById(R.id.wholeLayout);
        search = findViewById(R.id.search);
        initProducts();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());
            }
        });

    }

    private void initProducts() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        getCartids();

        adapter = new ProductListAdapter(this, itemList, productIdList, new AddToCartCallback() {
            @Override
            public void onAddToCart(Product product) {
                addToCartProduct(product);
            }
        });
        recyclerView.setAdapter(adapter);
        getProductsDataFromDB();
    }

    private void getCartids() {
        HashMap<Integer, Integer> map = SharedPrefs.getCartMenuIds();
        if (map != null && map.size() > 0) {
            for (Map.Entry me : map.entrySet()) {
                System.out.println("Key: " + me.getKey() + " & Value: " + me.getValue());
                productIdList.add(me.getValue());
            }
            if (adapter != null) {
                adapter.setProductIdList(productIdList);
            }
        }

    }

    private void addToCartProduct(final Product product) {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AddToCartResponse> call = getResponse.addToCart(
                SharedPrefs.getToken(), "" + product.getId()
        );
        call.enqueue(new Callback<AddToCartResponse>() {
            @Override
            public void onResponse(Call<AddToCartResponse> call, Response<AddToCartResponse> response) {
                if (response.isSuccessful()) {
                    wholeLayout.setVisibility(View.GONE);
                    AddToCartResponse object = response.body();
                    if (object != null) {
                        if (object.getMeta().getMessage().equalsIgnoreCase("Successfully Added")) {
                            CommonUtils.showToast(object.getMeta().getMessage());
                            HashMap<Integer, Integer> map = SharedPrefs.getCartMenuIds();
                            if (map != null) {
                                map.put(product.getId(), product.getId());
                                SharedPrefs.setCartMenuIds(map);
                            } else {
                                map = new HashMap<>();
                                map.put(product.getId(), product.getId());
                                SharedPrefs.setCartMenuIds(map);
                            }
                            getCartids();
                        }
                    } else {
                        CommonUtils.showToast("There is some error");
                    }
                }

            }

            @Override
            public void onFailure(Call<AddToCartResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast(t.getMessage());
            }
        });
    }

    private void getProductsDataFromDB() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ProductResponse> call = getResponse.getProducts(
                SharedPrefs.getToken()
        );
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    ProductResponse object = response.body();
                    if (object != null && object.getData() != null) {
                        if (object.getData().size() > 0) {
                            for (Product product : object.getData()) {
                                if (product.getcId().equalsIgnoreCase("" + categoryId)) {
                                    itemList.add(product);
                                }
                            }
//                            itemList = object.getData();
                            adapter.updateList(itemList);
                        } else {
                            CommonUtils.showToast("No products Data");
                        }
                    } else {
                        CommonUtils.showToast("There is some error");
                    }
                }

            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            startActivity(new Intent(ListOfProducts.this, CartActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
