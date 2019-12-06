package com.saffron.club.NetworkResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Models.Category;
import com.saffron.club.Models.Meta;
import com.saffron.club.Models.Success;

import java.util.List;

public class CategoryResponse {


    @SerializedName("data")
    @Expose
    private List<Category> data = null;
    @SerializedName("meta")
    @Expose
    private Meta meta;

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
