package com.saffron.club.NetworkResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Models.Meta;
import com.saffron.club.Models.Success;

public class PlaceOrderResponse {

    @SerializedName("data")
    @Expose
    private Success success;
    @SerializedName("meta")
    @Expose
    private Meta meta;

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
