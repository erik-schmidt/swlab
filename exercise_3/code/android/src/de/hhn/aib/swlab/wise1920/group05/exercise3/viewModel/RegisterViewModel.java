package de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.net.HttpURLConnection;
import java.util.Objects;

import de.hhn.aib.swlab.wise1920.group05.exercise3.model.User;
import de.hhn.aib.swlab.wise1920.group05.exercise3.network.RetrofitAPI;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.listener.RegisterListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterViewModel extends ViewModel {

    private String username;
    private String password;
    private RegisterListener registerListener;
    private final RetrofitAPI retrofitAPI;
    private final MutableLiveData<User> userLiveData;


    public RegisterViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
        userLiveData = new MutableLiveData<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void onRegisterButtonClick(){
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        retrofitAPI.createUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == HttpURLConnection.HTTP_OK || response.code() == HttpURLConnection.HTTP_CREATED) {
                    loginUser(user);
                } else if(response.code() == HttpURLConnection.HTTP_CONFLICT) {
                    registerListener.onFailure("Username already taken");
                } else {
                    Log.i("Error Register", "Response Code: " + response.code());
                    registerListener.onFailure("Please fill in all fields");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                registerListener.onNoConnection("No connection");
                Log.i("Registration Response", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void onCancelButtonClick(){
        registerListener.onCancel();
    }

    public void setRegisterListener(RegisterListener registerListener) {
        this.registerListener = registerListener;
    }

    private void loginUser(User user) {
        retrofitAPI.loginUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == HttpURLConnection.HTTP_OK) {
                    userLiveData.setValue(user);
                    registerListener.onSuccess("Registration successfull", userLiveData);
                } else if(response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
                    registerListener.onFailure("Invalid username/password");
                } else {
                    registerListener.onFailure("Login failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                registerListener.onFailure("No Connection");
            }
        });
    }
}
