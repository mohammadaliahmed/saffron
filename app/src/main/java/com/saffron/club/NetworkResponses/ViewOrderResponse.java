package com.saffron.club.NetworkResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Models.Meta;
import com.saffron.club.Models.OrderModel;

import java.util.List;

public class ViewOrderResponse {
    @SerializedName("data")
    @Expose
    private OrderModel data;
    @SerializedName("meta")
    @Expose
    private Meta meta;

    public OrderModel getData() {
        return data;
    }

    public void setData(OrderModel data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
