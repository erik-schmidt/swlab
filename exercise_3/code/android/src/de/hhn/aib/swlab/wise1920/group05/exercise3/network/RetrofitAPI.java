package de.hhn.aib.swlab.wise1920.group05.exercise3.network;

import de.hhn.aib.swlab.wise1920.group05.exercise3.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitAPI {

    String URL = "https://user.ex3.swlab-hhn.de/";

    @POST("user")
    Call<Void> createUser(@Body User user);

    @POST("user/login")
    Call<Void> loginUser(@Body User user);
}
