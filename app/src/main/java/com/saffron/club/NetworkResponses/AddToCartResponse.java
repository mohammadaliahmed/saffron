package com.saffron.club.NetworkResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Models.Category;
import com.saffron.club.Models.Meta;

import java.util.List;

public class AddToCartResponse {


    @SerializedName("data")
    @Expose
    private Object data;
    @SerializedName("meta")
    @Expose
    private Meta meta;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
