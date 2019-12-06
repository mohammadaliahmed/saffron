package com.saffron.club.NetworkResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Models.Meta;
import com.saffron.club.Models.Success;
import com.saffron.club.Models.UserModel;

public class UserDetailsResponse {

    @SerializedName("data")
    @Expose
    private UserModel user;
    @SerializedName("meta")
    @Expose
    private Meta meta;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
