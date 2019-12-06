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
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;
import com.saffron.club.Utils.SharedPrefs;
import com.saffron.club.Utils.UserClient;

import java.util.ArrayList;
import java.util.List;

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

public class ChooseTableFragment extends Fragment {

    Context context;
    RecyclerView recyclerView;
    ChooseTableAdapter adapter;
    private List<Table> itemList = new ArrayList<>();
    RelativeLayout wholeLayout;
    public static ConfirmBookingResponse confirmBookingResponse;

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
        });
        recyclerView.setAdapter(adapter);


        return view;
    }

    private void chooseMenuApi(Table table) {
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


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                if (reservation.getTables() != null) {
                    itemList = reservation.getTables();
                    adapter.setItemList(itemList);
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
