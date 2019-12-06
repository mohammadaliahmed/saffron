package com.saffron.club.NetworkResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Models.Category;
import com.saffron.club.Models.Data;
import com.saffron.club.Models.Meta;
import com.saffron.club.Models.Table;

import java.util.List;

public class MakeReservationResponse {
    @SerializedName("data")
    @Expose
    private List<Table> tables = null;
    @SerializedName("meta")
    @Expose
    private Meta meta;

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
