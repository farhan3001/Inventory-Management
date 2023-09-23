package com.example.qtitest.domain;

import com.example.qtitest.data.AssetGetResponse;
import com.example.qtitest.data.AssetItems;
import com.example.qtitest.data.AssetRequest;
import com.example.qtitest.data.AssetResponse;
import com.example.qtitest.data.AssetResponseHome;
import com.example.qtitest.data.LocationResponseHome;
import com.example.qtitest.data.LoginRequest;
import com.example.qtitest.data.LoginResponse;
import com.example.qtitest.data.LogoutResponse;
import com.example.qtitest.data.RefreshedToken;
import com.example.qtitest.data.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Services {
    @POST("auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("auth/logout")
    Call<LogoutResponse> logoutUser(@Header("Authorization") String token);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("auth/me")
    Call<UserResponse> user(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/token")
    Call<RefreshedToken> refreshToken(@Field("username") String username, @Field("password") String password);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("home/agg-asset-by-location/")
    Call<LocationResponseHome> locationResponseHome(@Header("Authorization") String token);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("home/agg-asset-by-status/")
    Call<AssetResponseHome> assetStatusHome(@Header("Authorization") String token);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("asset/")
    Call<AssetResponse> createAsset(@Header("Authorization") String token, @Body AssetRequest assetRequest);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("asset/")
    Call<AssetItems> getAllAsset(@Header("Authorization") String token,
                                 @Query("page") int page,
                                 @Query("page_size") int limit);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("asset/{id}")
    Call<AssetGetResponse> getAssetById(@Header("Authorization") String token,
                                        @Path(value = "id", encoded = true) String id);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @PUT("asset/{id}")
    Call<AssetResponse> editAsset(@Header("Authorization") String token,
                                  @Path(value = "id", encoded = true) String id,
                                  @Body AssetRequest assetRequest);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @DELETE("asset/{id}")
    Call<String> deleteAsset(@Header("Authorization") String token,
                             @Path(value = "id", encoded = true) String id);
}
