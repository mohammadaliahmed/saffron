package com.saffron.club.NetworkResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Models.OrderModel;

import java.util.List;

public class ViewOrderResponse {
    @SerializedName("orderDetail")
    @Expose
    private OrderModel orderDetail;

    public OrderModel getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(OrderModel orderDetail) {
        this.orderDetail = orderDetail;
    }
}
