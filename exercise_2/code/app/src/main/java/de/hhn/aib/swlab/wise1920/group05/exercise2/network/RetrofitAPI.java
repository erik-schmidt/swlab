package de.hhn.aib.swlab.wise1920.group05.exercise2.network;

import android.location.Location;

import java.util.List;

import de.hhn.aib.swlab.wise1920.group05.exercise2.model.MyLocation;
import de.hhn.aib.swlab.wise1920.group05.exercise2.model.OtherUser;
import de.hhn.aib.swlab.wise1920.group05.exercise2.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitAPI {
    String URL = "https://swlab.iap.hs-heilbronn.de/ex2/api/v0.3/";

    @POST("user")
    Call<Void> registerUser(@Body User user);

    @POST("user/login")
    Call<User> loginUser(@Body User user);

    @PUT("user/{userID}")
    Call<Void> updateUser(@Header ("Authorization") String jwt, @Path("userID") String userID, @Body User user);

    @POST("user/{userID}/location")
    Call<Void> updateLocation(@Header("Authorization") String jwt, @Path("userID") String userID, @Body Location location);

    @GET("user/{userID}/location")
    Call<List<MyLocation>> getLocations(@Header("Authorization") String jwt, @Path("userID") String userID);

    @GET("location/{radius}/{lat}/{long}")
    Call<List<OtherUser>> getLocations(@Header("Authorization") String jwt, @Path("radius") double radius, @Path("lat") double latitude, @Path("long") double longitude);
}
