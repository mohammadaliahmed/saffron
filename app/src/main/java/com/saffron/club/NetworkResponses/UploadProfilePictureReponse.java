package com.saffron.club.NetworkResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saffron.club.Models.Meta;
import com.saffron.club.Models.UserModel;

public class UploadProfilePictureReponse {

    @SerializedName("data")
    @Expose
    private UserModel userModel;
    @SerializedName("meta")
    @Expose
    private Meta meta;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
