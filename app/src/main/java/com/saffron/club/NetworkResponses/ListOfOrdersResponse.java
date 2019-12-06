package com.saffron.club.NetworkResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Models.Category;
import com.saffron.club.Models.OrderModel;

import java.util.List;

public class ListOfOrdersResponse {
    @SerializedName("orders")
    @Expose
    private List<OrderModel> orders = null;

    public List<OrderModel> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderModel> orders) {
        this.orders = orders;
    }
}
