package com.saffron.club.Utils;


import com.google.gson.JsonObject;
import com.saffron.club.Models.BookingModel;
import com.saffron.club.Models.MenuModel;
import com.saffron.club.Models.Table;
import com.saffron.club.NetworkResponses.AddToCartResponse;
import com.saffron.club.NetworkResponses.CategoryResponse;
import com.saffron.club.NetworkResponses.ChangePasswordResponse;
import com.saffron.club.NetworkResponses.ConfirmBookingResponse;
import com.saffron.club.NetworkResponses.LoginResponse;
import com.saffron.club.NetworkResponses.ListOfOrdersResponse;
import com.saffron.club.NetworkResponses.MakeReservationResponse;
import com.saffron.club.NetworkResponses.PlaceOrderResponse;
import com.saffron.club.NetworkResponses.ProductResponse;
import com.saffron.club.NetworkResponses.RemoveMenuResponse;
import com.saffron.club.NetworkResponses.SaveAddressResponse;
import com.saffron.club.NetworkResponses.SaveUserResponse;
import com.saffron.club.NetworkResponses.SignupResponse;
import com.saffron.club.NetworkResponses.UploadProfilePictureReponse;
import com.saffron.club.NetworkResponses.UserDetailsResponse;
import com.saffron.club.NetworkResponses.ViewOrderResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface UserClient {


    @POST("/web/api/login")
    @FormUrlEncoded
    Call<LoginResponse> loginUser(
            @Field("email") String email,
            @Field("password") String password

    );

    @POST("/web/api/signup")
    @FormUrlEncoded
    Call<SignupResponse> signUp(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("c_password") String cpassword

    );


    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/web/api/details")
    Call<UserDetailsResponse> userDetails(
            @Header("Authorization") String auth

    );

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/web/api/category")
    Call<CategoryResponse> getCategories(
            @Header("Authorization") String auth

    );

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/web/api/products")
    Call<ProductResponse> getProducts(
            @Header("Authorization") String auth

    );

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/web/api/orders/all")
    Call<ListOfOrdersResponse> getOrders(
            @Header("Authorization") String auth

    );

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/web/api/orders/{id}")
    Call<ViewOrderResponse> getOrdersDetails(
            @Header("Authorization") String auth,
            @Path(value = "id", encoded = true) String userId

    );


    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/savegeneral")
    @FormUrlEncoded
    Call<SaveUserResponse> saveGeneral(
            @Header("Authorization") String auth,
            @Field("name") String name,
            @Field("cell1") String cell1,
            @Field("cell2") String cell2,
            @Field("cell3") String cell3


    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/savepassword")
    @FormUrlEncoded
    Call<ChangePasswordResponse> changePassword(
            @Header("Authorization") String auth,
            @Field("password") String password,
            @Field("password_confirmation") String confirmPassword


    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/saveaddress")
    @FormUrlEncoded
    Call<SaveAddressResponse> saveAddress(
            @Header("Authorization") String auth,
            @Field("address1") String address1,
            @Field("address2") String address2,
            @Field("address3") String address3


    );

    @Headers({"Accept:application/json"})
    @POST("/web/api/saveimage")
    @Multipart
    Call<UploadProfilePictureReponse> uploadPicture(
            @Header("Authorization") String auth,
            @Part MultipartBody.Part file, @Part("profile") RequestBody name

    );


    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/reservations")
    @FormUrlEncoded
    Call<MakeReservationResponse> bookTable(
            @Header("Authorization") String auth,
            @Field("date") String date,
            @Field("time") String time,
            @Field("timeto") String timeTo,
            @Field("persons") String persons


    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/confirm")
    Call<ConfirmBookingResponse> confirmBooking(
            @Header("Authorization") String auth


    );


    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/confirm")
    Call<ConfirmBookingResponse> addMenuToCart(
            @Header("Authorization") String auth,
            @Field("id") String menuId

    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/addtable")
    @FormUrlEncoded
    Call<ConfirmBookingResponse> chooseTable(
            @Header("Authorization") String auth,
            @Field("id") String tableId,
            @Field("date") String date,
            @Field("time") String time,
            @Field("persons") String persons

    );


    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/cart")
    @FormUrlEncoded
    Call<ConfirmBookingResponse> confirmBooking(
            @Header("Authorization") String auth,
            @Field("id") String menuId


    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/cart")
    @FormUrlEncoded
    Call<AddToCartResponse> addToCart(
            @Header("Authorization") String auth,
            @Field("id") String id,
            @Field("eid") String eid

    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/addExtraToCart")
    @FormUrlEncoded
    Call<AddToCartResponse> addExtraToCart(
            @Header("Authorization") String auth,
            @Field("id") String id,
            @Field("eid") String eid

    );



    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/removeFromCart")
    @FormUrlEncoded
    Call<AddToCartResponse> removeFromCart(
            @Header("Authorization") String auth,
            @Field("id") String id

    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/removemenu")
    @FormUrlEncoded
    Call<RemoveMenuResponse> removeMenu(
            @Header("Authorization") String auth,
            @Field("id") String id

    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/web/api/removetable")
    @FormUrlEncoded
    Call<RemoveMenuResponse> removeTable(
            @Header("Authorization") String auth,
            @Field("id") String id

    );

    //    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
//    @POST("/web/api/placeorder{data}")
//    Call<PlaceOrderResponse> placeOrder(
//            @Header("Authorization") String auth,
//            @Path(value = "data", encoded = true) String data,
//            @Url String apiname
//
//
//    );
    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST()
    Call<PlaceOrderResponse> placeOrder(
            @Header("Authorization") String auth,
            @Url String apiname


    );

    @POST("/braintreeCheckout.php")
    @FormUrlEncoded
    Call<ResponseBody> braintreeCheckout(
            @Field("payment_method_nonce") String nonce

    );

    @GET("/braintreetoken.php")
    Call<ResponseBody> geTokken();

}
