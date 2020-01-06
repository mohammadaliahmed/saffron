package com.saffron.club.Activities.ReservationManagement.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.saffron.club.Activities.Callbacks.AddToCartCallback;
import com.saffron.club.Activities.CartManagement.CartActivity;
import com.saffron.club.Activities.ListOfProducts;
import com.saffron.club.Activities.MainActivity;
import com.saffron.club.Activities.ReservationManagement.BookTable;
import com.saffron.club.Activities.ReservationManagement.ChooseMenuAdapter;
import com.saffron.club.Activities.ReservationManagement.ChooseTableAdapter;
import com.saffron.club.Activities.ReservationManagement.MenuFragmentCategoryAdapter;
import com.saffron.club.Adapters.ExtrasProductAdapter;
import com.saffron.club.Adapters.ProductListAdapter;
import com.saffron.club.Adapters.VariationListAdapter;
import com.saffron.club.Models.Category;
import com.saffron.club.Models.Extra;
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
import de.hdodenhof.circleimageview.CircleImageView;
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

    public void getDataaFromServer() {

        categoryAdapter = new MenuFragmentCategoryAdapter(context, MainActivity.itemList, new MenuFragmentCategoryAdapter.MenuFragmentCategoryCallbacks() {
            @Override
            public void onOptionChosen(Category category) {
                Constants.CATEGORY_CHOSEN_ID = "" + category.getId();
                getProductsFromDB();
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        categoryAdapter.setItemList(MainActivity.itemList);
        recyclerView.setAdapter(categoryAdapter);
    }

    private void getProductsFromDB() {
        productsList.clear();
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
        Constants.MENU_STEP = 2;
        getCartids();
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        productListAdapter = new ProductListAdapter(context, productsList, productIdList, new AddToCartCallback() {
            @Override
            public void onAddToCart(Product product) {
                showExtrasAlert(product);

//                addToCartProduct(product);
            }

            @Override
            public void onRemoveFromCart(Product product1) {
                removeFromCartProduct(product1);

            }
        });
        recyclerView.setAdapter(productListAdapter);
    }

    private void removeFromCartProduct(final Product product) {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AddToCartResponse> call = getResponse.addToCart(
                SharedPrefs.getToken(), "" + product.getId(), "" + 0
        );
        call.enqueue(new Callback<AddToCartResponse>() {
            @Override
            public void onResponse(Call<AddToCartResponse> call, Response<AddToCartResponse> response) {
                if (response.isSuccessful()) {
                    wholeLayout.setVisibility(View.GONE);
                    AddToCartResponse object = response.body();
                    if (object != null) {
                        if (object.getMeta().getMessage().equalsIgnoreCase("Successfully Added")) {
//                            dialog.dismiss();
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
                        } else if (object.getMeta().getMessage().equalsIgnoreCase("Successfully Removed")) {
//                            dialog.dismiss();
                            CommonUtils.showToast(object.getMeta().getMessage());
                            HashMap<Integer, Integer> map = SharedPrefs.getCartMenuIds();
                            if (map != null) {
                                map.remove(product.getId());
                                SharedPrefs.setCartMenuIds(map);
                            } else {
                                map = new HashMap<>();
                                map.remove(product.getId());
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

    private void showExtrasAlert(final Product product) {
        final Dialog dialog = new Dialog(context);
        final int[] eid = {0};
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_extras_layout, null);

        dialog.setContentView(layout);
        CircleImageView picture = layout.findViewById(R.id.picture);
        TextView title = layout.findViewById(R.id.title);
        LinearLayout variationLayout = layout.findViewById(R.id.variationLayout);
        Button addToCart = layout.findViewById(R.id.addToCart);
        ImageView close = layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        RecyclerView variation = layout.findViewById(R.id.variationRecycler);
        RecyclerView extrasRecycler = layout.findViewById(R.id.extrasRecycler);
        LinearLayout extrasLayout = layout.findViewById(R.id.extrasLayout);

        title.setText(product.getName());
        Glide.with(context).load(AppConfig.BASE_URL_Image + product.getImage()).into(picture);
        final ArrayList<Integer> list = new ArrayList<>();

        if (product.getExtras() != null && product.getExtras().size() > 0) {
            variationLayout.setVisibility(View.VISIBLE);
            VariationListAdapter adapter2 = new VariationListAdapter(context, product.getExtras(), new VariationListAdapter.ExtrasCallback() {
                @Override
                public void onSelect(Extra extra) {
                    eid[0] = extra.getId();
                    list.add(extra.getId());

                    CommonUtils.showToast(extra.getName());
                }

                @Override
                public void onRemove(Extra extra) {
                    list.remove(extra.getId());

                }
            });
            variation.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            variation.setAdapter(adapter2);
        }
        if (Integer.parseInt(Constants.CATEGORY_CHOSEN_ID) != 5) {
            extrasLayout.setVisibility(View.VISIBLE);
            extrasRecycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            ExtrasProductAdapter extrasProductAdapter = new ExtrasProductAdapter(context, MainActivity.additionalItems, new ExtrasProductAdapter.AddExtraCallback() {
                @Override
                public void onAdd(Product product) {
                    addExtraToCartProduct(product);
                }

                @Override
                public void onRemove(Product product) {
                    removeFromCartProduct(product);
                }
            });

            extrasRecycler.setAdapter(extrasProductAdapter);
        }
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product.getExtras() != null && product.getExtras().size() > 0) {
                    if (eid[0] == 0) {
                        CommonUtils.showToast("Please select your meal size");
                    } else {
                        addToCartProduct(product, eid[0], dialog);

                    }
                } else {
                    addToCartProduct(product, eid[0], dialog);
                }
            }
        });


        dialog.show();

    }

    private void addExtraToCartProduct(final Product product) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AddToCartResponse> call = getResponse.addExtraToCart(
                SharedPrefs.getToken(), "" + product.getId(), "" + 0
        );
        call.enqueue(new Callback<AddToCartResponse>() {
            @Override
            public void onResponse(Call<AddToCartResponse> call, Response<AddToCartResponse> response) {
                if (response.isSuccessful()) {
                    AddToCartResponse object = response.body();
                    if (object != null) {

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

    private void addToCartProduct(final Product product, int edi, final Dialog dialog) {
        dialog.dismiss();
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AddToCartResponse> call = getResponse.addToCart(
                SharedPrefs.getToken(), "" + product.getId(), "" + edi
        );
        call.enqueue(new Callback<AddToCartResponse>() {
            @Override
            public void onResponse(Call<AddToCartResponse> call, Response<AddToCartResponse> response) {
                if (response.isSuccessful()) {
                    wholeLayout.setVisibility(View.GONE);
                    AddToCartResponse object = response.body();
                    if (object != null) {
                        if (object.getMeta().getMessage().equalsIgnoreCase("Successfully Added")) {
                            dialog.dismiss();
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
                        dialog.dismiss();
                        CommonUtils.showToast("There is some error");
                    }
                }

            }

            @Override
            public void onFailure(Call<AddToCartResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast(t.getMessage());
                dialog.dismiss();
            }
        });
    }


    private void getCartids() {
        productIdList.clear();
        HashMap<Integer, Integer> map = SharedPrefs.getCartMenuIds();
        if (map != null) {
            if (map.size() > 0) {
                for (Map.Entry me : map.entrySet()) {
                    System.out.println("Key: " + me.getKey() + " & Value: " + me.getValue());
                    productIdList.add(me.getValue());
                }
                if (productListAdapter != null) {
                    productListAdapter.setProductIdList(productIdList);
                }
            } else {
                productIdList = new ArrayList();
                if (productListAdapter != null) {
                    productListAdapter.setProductIdList(productIdList);
                }
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
