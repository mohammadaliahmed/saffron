package com.saffron.club.Activities.ReservationManagement.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.saffron.club.Activities.MainActivity;
import com.saffron.club.Activities.ReservationManagement.BookTable;
import com.saffron.club.Models.Data;
import com.saffron.club.Models.Table;
import com.saffron.club.NetworkResponses.MakeReservationResponse;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;
import com.saffron.club.Utils.SharedPrefs;
import com.saffron.club.Utils.UserClient;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindTableFragment extends Fragment {

    Context context;
    EditText name, guestCount, dateTime;
    Button confirmBooking;
    TextView cancel;
    ImageView editDate;
    RelativeLayout wholeLayout;

    ImageView table;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private String timeSet;
    public static String persons;
    public static MakeReservationResponse reservation;
    public static String date;
    public static String time;

    public FindTableFragment() {
    }

    @SuppressLint("ValidFragment")
    public FindTableFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_find_table, container, false);
        guestCount = view.findViewById(R.id.guestCount);
        name = view.findViewById(R.id.name);
        dateTime = view.findViewById(R.id.dateTime);
        confirmBooking = view.findViewById(R.id.confirmBooking);
        cancel = view.findViewById(R.id.cancel);
        editDate = view.findViewById(R.id.editDate);
        wholeLayout = view.findViewById(R.id.wholeLayout);


        name.setText(SharedPrefs.getUserModel().

                getName());
        dateTime.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                showDateeDialog(dateTime);
            }
        });


        editDate.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                showDateeDialog(dateTime);
            }

        });


        confirmBooking.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (dateTime.getText().length() == 0) {
                    dateTime.setError("Please select date and time for booking");
                } else if (guestCount.getText().length() == 0) {
                    guestCount.setError("Enter number of persons");
                } else {
                    persons = guestCount.getText().toString();
                    confirmBookingNow(wholeLayout);
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

            }
        });


        return view;


    }

    private void confirmBookingNow(final RelativeLayout wholeLayout) {
        wholeLayout.setVisibility(View.VISIBLE);
         date = mYear + "-" + mMonth + "-" + mDay;
         time = mHour + ":" + mMinute + timeSet;
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<MakeReservationResponse> call = getResponse.bookTable(
                SharedPrefs.getToken(),
                date,
                time,
                persons

        );
        call.enqueue(new Callback<MakeReservationResponse>() {
            @Override
            public void onResponse(Call<MakeReservationResponse> call, Response<MakeReservationResponse> response) {
                if (response.code() == 200) {
                    wholeLayout.setVisibility(View.GONE);

                    reservation = response.body();
                    if (reservation != null) {
                        List<Table> abc = reservation.getTables();
                        if (abc != null) {
                            BookTable.pager.setCurrentItem(1, true);
                        }
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override

            public void onFailure(Call<MakeReservationResponse> call, Throwable t) {
                t.printStackTrace();
                CommonUtils.showToast(t.getMessage());
                wholeLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showDateeDialog(final TextView dateTime) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear + 1;
                        mDay = dayOfMonth;

                        dateTime.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        showTimeAlert();

                    }

                    private void showTimeAlert() {
                        final Calendar c = Calendar.getInstance();
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        mHour = hourOfDay;
                                        mMinute = minute;
                                        timeSet = "";
                                        if (mHour > 12) {
                                            mHour -= 12;
                                            timeSet = "PM";
                                        } else if (mHour == 0) {
                                            mHour += 12;
                                            timeSet = "AM";
                                        } else if (mHour == 12) {
                                            timeSet = "PM";
                                        } else {
                                            timeSet = "AM";
                                        }

                                        String min = "";
                                        if (mMinute < 10)
                                            min = "0" + mMinute;
                                        else
                                            min = String.valueOf(mMinute);

                                        String aTime = new StringBuilder().append(mHour).append(':')
                                                .append(min).append(" ").append(timeSet).toString();
                                        dateTime.setText(mDay + "-" + (mMonth + 1) + "-" + mYear + " " + aTime);
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }
}
