package com.saffron.club.Activities.ReservationManagement.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Activities.ReservationManagement.BookTable;
import com.saffron.club.Activities.ReservationManagement.ChooseTableAdapter;
import com.saffron.club.Models.Meta;
import com.saffron.club.Models.Table;
import com.saffron.club.NetworkResponses.ConfirmBookingResponse;
import com.saffron.club.NetworkResponses.MakeReservationResponse;
import com.saffron.club.NetworkResponses.RemoveMenuResponse;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.saffron.club.Activities.ReservationManagement.Fragments.FindTableFragment.reservation;

public class ChooseTableFragment extends Fragment {

    Context context;
    RecyclerView recyclerView;
    ChooseTableAdapter adapter;
    private List<Table> itemList = new ArrayList<>();
    RelativeLayout wholeLayout;
    public static ConfirmBookingResponse confirmBookingResponse;
    private ArrayList productIdList = new ArrayList();

    public ChooseTableFragment() {
    }

    @SuppressLint("ValidFragment")
    public ChooseTableFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_choose_table, container, false);
        wholeLayout = view.findViewById(R.id.wholeLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        adapter = new ChooseTableAdapter(context, itemList, new ChooseTableAdapter.ChooseTableCallback() {
            @Override
            public void onTableChosen(Table table) {
//                CommonUtils.showToast(table.getName());
                chooseMenuApi(table);
            }

            @Override
            public void onTableRemove(Table table) {
                removeTableAPI(table);
            }
        });
        recyclerView.setAdapter(adapter);


        return view;
    }

    private void removeTableAPI(final Table table) {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<RemoveMenuResponse> call = getResponse.removeTable(
                SharedPrefs.getToken(),
                "" + table.getId()
        );
        call.enqueue(new Callback<RemoveMenuResponse>() {
            @Override
            public void onResponse(Call<RemoveMenuResponse> call, Response<RemoveMenuResponse> response) {
                if (response.code() == 200) {
                    wholeLayout.setVisibility(View.GONE);

                    RemoveMenuResponse abc = response.body();
                    if (abc.getMeta().getMessage().equalsIgnoreCase("Successfully Removed")) {
                        CommonUtils.showToast("Table Removed");
                        HashMap<Integer, Integer> map = SharedPrefs.getTableIds();

                        if (map != null) {
                            map.remove(table.getId());
                            SharedPrefs.setTableIds(map);
                        } else {
                            map = new HashMap<>();
                            map.remove(table.getId());
                            SharedPrefs.setTableIds(map);
                        }
                        getTableIdss();
                    }
                }
            }

            @Override
            public void onFailure(Call<RemoveMenuResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
                wholeLayout.setVisibility(View.GONE);

            }
        });
    }

    private void chooseMenuApi(final Table table) {
        wholeLayout.setVisibility(View.VISIBLE);

        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ConfirmBookingResponse> call = getResponse.chooseTable(
                SharedPrefs.getToken(), "" + table.getId(),
                FindTableFragment.date,
                FindTableFragment.time,
                FindTableFragment.persons
        );
        call.enqueue(new Callback<ConfirmBookingResponse>() {
            @Override
            public void onResponse(Call<ConfirmBookingResponse> call, Response<ConfirmBookingResponse> response) {
                if (response.code() == 200) {
                    wholeLayout.setVisibility(View.GONE);

                    confirmBookingResponse = response.body();
                    if (confirmBookingResponse != null) {
                        BookTable.pager.setCurrentItem(2, true);
                        HashMap<Integer, Integer> map = SharedPrefs.getTableIds();
                        if (map != null) {
                            map.put(table.getId(), table.getId());
                            SharedPrefs.setTableIds(map);
                        } else {
                            map = new HashMap<>();
                            map.put(table.getId(), table.getId());
                            SharedPrefs.setTableIds(map);
                        }
                        getTableIdss();
                    }
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

    private void getTableIdss() {
        HashMap<Integer, Integer> map = SharedPrefs.getTableIds();
        if (map != null) {
            if (map.size() > 0) {
                for (Map.Entry me : map.entrySet()) {
//                    System.out.println("Key: " + me.getKey() + " & Value: " + me.getValue());
                    productIdList.add(me.getValue());
                }
                if (adapter != null) {
                    adapter.setProductIdList(productIdList);
                }
            } else {
                productIdList = new ArrayList();
                if (adapter != null) {
                    adapter.setProductIdList(productIdList);
                }
            }
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                if (reservation.getTables() != null) {
                    itemList = reservation.getTables();
                    adapter.setItemList(itemList);
                    getTableIdss();
                }
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
