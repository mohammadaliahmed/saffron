package com.saffron.club.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaypalResponse {
//    @SerializedName("client")
//    @Expose
//    private Client client;
    @SerializedName("response")
    @Expose
    private ResponsePp response;
    @SerializedName("response_type")
    @Expose
    private String responseType;

    public ResponsePp getResponse() {
        return response;
    }

    public void setResponse(ResponsePp response) {
        this.response = response;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
}

