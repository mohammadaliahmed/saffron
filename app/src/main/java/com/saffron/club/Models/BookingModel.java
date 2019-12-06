package com.saffron.club.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("u_id")
    @Expose
    private String uId;
    @SerializedName("t_id")
    @Expose
    private String tId;
    @SerializedName("persons")
    @Expose
    private Object persons;
    @SerializedName("time")
    @Expose
    private Object time;
    @SerializedName("date")
    @Expose
    private Object date;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public Object getPersons() {
        return persons;
    }

    public void setPersons(Object persons) {
        this.persons = persons;
    }

    public Object getTime() {
        return time;
    }

    public void setTime(Object time) {
        this.time = time;
    }

    public Object getDate() {
        return date;
    }

    public void setDate(Object date) {
        this.date = date;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
