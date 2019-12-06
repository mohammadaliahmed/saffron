package com.saffron.club.Models;

import android.view.Menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfirmBookingModel {
    @SerializedName("bookings")
    @Expose
    private List<BookingModel> bookings = null;
    @SerializedName("menu")
    @Expose
    private List<MenuModel> menu = null;

    public List<BookingModel> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingModel> bookings) {
        this.bookings = bookings;
    }

    public List<MenuModel> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuModel> menu) {
        this.menu = menu;
    }
}
