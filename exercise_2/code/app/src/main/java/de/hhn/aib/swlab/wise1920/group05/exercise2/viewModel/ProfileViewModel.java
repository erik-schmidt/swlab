package de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel;

import android.view.View;
import androidx.lifecycle.ViewModel;
import java.net.HttpURLConnection;
import de.hhn.aib.swlab.wise1920.group05.exercise2.model.User;
import de.hhn.aib.swlab.wise1920.group05.exercise2.network.RetrofitAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileViewModel extends ViewModel {
    private String username;
    private String password;
    private String description;
    private String token;
    private String userID;
    private final RetrofitAPI retrofitAPI;
    private WebserviceListener webserviceListener;

    public ProfileViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWebserviceListener(WebserviceListener webserviceListener) {
        this.webserviceListener = webserviceListener;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void onSaveClick(View view){
        retrofitAPI.updateUser(token, userID, new User(userID, username, password, description)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == HttpURLConnection.HTTP_OK){
                    webserviceListener.onSuccess("Profile saved.");
                } else if (response.code() == HttpURLConnection.HTTP_NOT_MODIFIED){
                    webserviceListener.onFailure("No changes.");
                } else if (response.code() == HttpURLConnection.HTTP_FORBIDDEN){
                    webserviceListener.onTokenExpired("Session expired. Login again.");
                } else {
                    webserviceListener.onFailure("Change failed");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                webserviceListener.onNoConnection("No Server Connection");
            }
        });

    }
}
