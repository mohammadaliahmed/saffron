package com.saffron.club.Activities.ReservationManagement.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.saffron.club.Activities.Callbacks.AddToCartCallback;
import com.saffron.club.Activities.CartManagement.CartActivity;
import com.saffron.club.Activities.ListOfProducts;
import com.saffron.club.Activities.MainActivity;
import com.saffron.club.Activities.ReservationManagement.BookTable;
import com.saffron.club.Activities.ReservationManagement.ChooseMenuAdapter;
import com.saffron.club.Activities.ReservationManagement.ChooseTableAdapter;
import com.saffron.club.Activities.ReservationManagement.MenuFragmentCategoryAdapter;
import com.saffron.club.Adapters.ProductListAdapter;
import com.saffron.club.Models.Category;
import com.saffron.club.Models.MenuModel;
import com.saffron.club.Models.Product;
import com.saffron.club.Models.Table;
import com.saffron.club.NetworkResponses.AddToCartResponse;
import com.saffron.club.NetworkResponses.CategoryResponse;
import com.saffron.club.NetworkResponses.ConfirmBookingResponse;
import com.saffron.club.NetworkResponses.ProductResponse;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;
import com.saffron.club.Utils.Constants;
import com.saffron.club.Utils.SharedPrefs;
import com.saffron.club.Utils.UserClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.saffron.club.Activities.ReservationManagement.Fragments.FindTableFragment.reservation;

public class ChooseMenuFragment extends Fragment {

    Context context;
    RecyclerView recyclerView;
    RelativeLayout wholeLayout;
    private List<Category> abc = new ArrayList<>();
    private MenuFragmentCategoryAdapter categoryAdapter;
    private ArrayList<Product> productsList = new ArrayList<>();
    ProductListAdapter productListAdapter;
    Button confirmBooking;
    private ArrayList productIdList = new ArrayList<>();

    public ChooseMenuFragment() {
    }

    @SuppressLint("ValidFragment")
    public ChooseMenuFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_choose_menu, container, false);
        wholeLayout = view.findViewById(R.id.wholeLayout);
        confirmBooking = view.findViewById(R.id.confirmBooking);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        getDataFromServer();
        confirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, CartActivity.class));

            }
        });
        return view;
    }

    private void getDataFromServer() {
        categoryAdapter = new MenuFragmentCategoryAdapter(context, abc, new MenuFragmentCategoryAdapter.MenuFragmentCategoryCallbacks() {
            @Override
            public void onOptionChosen(Category category) {
                Constants.CATEGORY_CHOSEN_ID = "" + category.getId();
                getProductsFromDB();

            }
        });
//        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(categoryAdapter);

    }

    private void getProductsFromDB() {
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
                                if (product.getcId().equalsIgnoreCase("" + Constants.CATEGORY_CHOSEN_ID)) {
                                    productsList.add(product);
                                }
                            }
//                            itemList = object.getData();
                            if (productsList.size() > 0) {
                                setupProductRecycler(productsList);
                            }
//                            adapter.updateList(itemList);
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

    private void setupProductRecycler(ArrayList<Product> productsList) {
        getCartids();
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        productListAdapter = new ProductListAdapter(context, productsList, productIdList, new AddToCartCallback() {
            @Override
            public void onAddToCart(Product product) {
                addToCartProduct(product);
            }
        });
        recyclerView.setAdapter(productListAdapter);
    }

    private void addToCartProduct(final Product product) {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AddToCartResponse> call = getResponse.addToCart(
                SharedPrefs.getToken(), "" + product.getId(),"0"
        );
        call.enqueue(new Callback<AddToCartResponse>() {
            @Override
            public void onResponse(Call<AddToCartResponse> call, Response<AddToCartResponse> response) {
                if (response.isSuccessful()) {
                    wholeLayout.setVisibility(View.GONE);
                    AddToCartResponse object = response.body();
                    if (object != null) {
                        if (object.getMeta().getMessage().equalsIgnoreCase("Successfully Added")) {
                            confirmBooking.setVisibility(View.VISIBLE);
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


    private void getCartids() {
        HashMap<Integer, Integer> map = SharedPrefs.getCartMenuIds();
        if (map != null && map.size() > 0) {
            for (Map.Entry me : map.entrySet()) {
                System.out.println("Key: " + me.getKey() + " & Value: " + me.getValue());
                productIdList.add(me.getValue());
            }
            if (productListAdapter != null) {
                productListAdapter.setProductIdList(productIdList);
            }
        }

    }

    private void chooseProductApi(MenuModel menu) {
        wholeLayout.setVisibility(View.VISIBLE);

        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ConfirmBookingResponse> call = getResponse.addMenuToCart(
                SharedPrefs.getToken(), "" + menu.getId()
        );
        call.enqueue(new Callback<ConfirmBookingResponse>() {
            @Override
            public void onResponse(Call<ConfirmBookingResponse> call, Response<ConfirmBookingResponse> response) {
                if (response.code() == 200) {
                    wholeLayout.setVisibility(View.GONE);

//                    confirmBookingResponse = response.body();
//                    if (confirmBookingResponse != null) {
//
//                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override

            public void onFailure(Call<ConfirmBookingResponse> call, Throwable t) {
                t.printStackTrace();
                CommonUtils.showToast(t.getMessage());
                wholeLayout.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {

                categoryAdapter.setItemList(MainActivity.itemList);

            } catch (Exception e) {

            }
            // ...
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }
}
