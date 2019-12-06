package com.saffron.club.NetworkResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Models.Category;
import com.saffron.club.Models.Meta;
import com.saffron.club.Models.Product;

import java.util.List;

public class ProductResponse {


    @SerializedName("data")
    @Expose
    private List<Product> data = null;
    @SerializedName("meta")
    @Expose
    private Meta meta;

    public List<Product> getData() {
        return data;
    }

    public void setData(List<Product> data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
