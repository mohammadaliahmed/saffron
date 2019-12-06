package com.saffron.club.NetworkResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Models.ConfirmBookingModel;
import com.saffron.club.Models.MenuModel;
import com.saffron.club.Models.Meta;
import com.saffron.club.Models.Table;

import java.util.List;

public class ConfirmBookingResponse {
    @SerializedName("data")
    @Expose
    private ConfirmBookingModel confirmBookingModel;
    @SerializedName("meta")
    @Expose
    private Meta meta;

    public ConfirmBookingModel getConfirmBookingModel() {
        return confirmBookingModel;
    }

    public void setConfirmBookingModel(ConfirmBookingModel confirmBookingModel) {
        this.confirmBookingModel = confirmBookingModel;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
