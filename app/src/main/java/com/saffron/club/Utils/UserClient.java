package com.saffron.club.Utils;


import com.saffron.club.NetworkResponses.AddToCartResponse;
import com.saffron.club.NetworkResponses.CategoryResponse;
import com.saffron.club.NetworkResponses.ChangePasswordResponse;
import com.saffron.club.NetworkResponses.ConfirmBookingResponse;
import com.saffron.club.NetworkResponses.LoginResponse;
import com.saffron.club.NetworkResponses.ListOfOrdersResponse;
import com.saffron.club.NetworkResponses.MakeReservationResponse;
import com.saffron.club.NetworkResponses.ProductResponse;
import com.saffron.club.NetworkResponses.RemoveMenuResponse;
import com.saffron.club.NetworkResponses.SaveAddressResponse;
import com.saffron.club.NetworkResponses.SaveUserResponse;
import com.saffron.club.NetworkResponses.SignupResponse;
import com.saffron.club.NetworkResponses.UploadProfilePictureReponse;
import com.saffron.club.NetworkResponses.UserDetailsResponse;
import com.saffron.club.NetworkResponses.ViewOrderResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserClient {


    @POST("/public/api/login")
    @FormUrlEncoded
    Call<LoginResponse> loginUser(
            @Field("email") String email,
            @Field("password") String password

    );

    @POST("/public/api/signup")
    @FormUrlEncoded
    Call<SignupResponse> signUp(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("c_password") String cpassword

    );


    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/public/api/details")
    Call<UserDetailsResponse> userDetails(
            @Header("Authorization") String auth

    );

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/public/api/category")
    Call<CategoryResponse> getCategories(
            @Header("Authorization") String auth

    );

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/public/api/products")
    Call<ProductResponse> getProducts(
            @Header("Authorization") String auth

    );

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/public/api/orders/all")
    Call<ListOfOrdersResponse> getOrders(
            @Header("Authorization") String auth

    );

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/public/api/orders/{id}")
    Call<ViewOrderResponse> getOrdersDetails(
            @Header("Authorization") String auth,
            @Path(value = "id", encoded = true) String userId

    );


    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/public/api/savegeneral")
    @FormUrlEncoded
    Call<SaveUserResponse> saveGeneral(
            @Header("Authorization") String auth,
            @Field("name") String name,
            @Field("cell1") String cell1,
            @Field("cell2") String cell2,
            @Field("cell3") String cell3


    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/public/api/savepassword")
    @FormUrlEncoded
    Call<ChangePasswordResponse> changePassword(
            @Header("Authorization") String auth,
            @Field("password") String password,
            @Field("password_confirmation") String confirmPassword


    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/public/api/saveaddress")
    @FormUrlEncoded
    Call<SaveAddressResponse> saveAddress(
            @Header("Authorization") String auth,
            @Field("address1") String address1,
            @Field("address2") String address2,
            @Field("address3") String address3


    );

    @Headers({"Accept:application/json"})
    @POST("/public/api/saveimage")
    @Multipart
    Call<UploadProfilePictureReponse> uploadPicture(
            @Header("Authorization") String auth,
            @Part MultipartBody.Part file, @Part("profile") RequestBody name

    );


    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/public/api/reservations")
    @FormUrlEncoded
    Call<MakeReservationResponse> bookTable(
            @Header("Authorization") String auth,
            @Field("date") String date,
            @Field("time") String time,
            @Field("persons") String persons


    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/public/api/confirm")
    Call<ConfirmBookingResponse> confirmBooking(
            @Header("Authorization") String auth


    );


    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/public/api/confirm")
    Call<ConfirmBookingResponse> addMenuToCart(
            @Header("Authorization") String auth,
            @Field("id") String menuId

    );
    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/public/api/addtable")
    @FormUrlEncoded
    Call<ConfirmBookingResponse> chooseTable(
            @Header("Authorization") String auth,
            @Field("id") String tableId,
            @Field("date") String date,
            @Field("time") String time,
            @Field("persons") String persons

    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/public/api/cart")
    @FormUrlEncoded
    Call<ConfirmBookingResponse> confirmBooking(
            @Header("Authorization") String auth,
            @Field("id") String menuId


    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/public/api/cart")
    @FormUrlEncoded
    Call<AddToCartResponse> addToCart(
            @Header("Authorization") String auth,
            @Field("id") String id,
            @Field("eid") String eid

    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/public/api/removemenu")
    @FormUrlEncoded
    Call<RemoveMenuResponse> removeMenu(
            @Header("Authorization") String auth,
            @Field("id") String id

    );

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept:application/json"})
    @POST("/public/api/removetable")
    @FormUrlEncoded
    Call<RemoveMenuResponse> removeTable(
            @Header("Authorization") String auth,
            @Field("id") String id

    );

    @GET("/braintreetoken.php")
    Call<ResponseBody> geTokken();

}
