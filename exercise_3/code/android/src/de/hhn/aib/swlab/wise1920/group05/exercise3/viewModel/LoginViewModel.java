package de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.net.HttpURLConnection;
import java.util.Objects;

import de.hhn.aib.swlab.wise1920.group05.exercise3.model.User;
import de.hhn.aib.swlab.wise1920.group05.exercise3.network.RetrofitAPI;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.listener.LoginListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginViewModel extends ViewModel {

    private String username;
    private String password;
    private LoginListener loginListener;
    private final RetrofitAPI retrofitAPI;
    private final MutableLiveData<User> userLiveData;

    public LoginViewModel() {
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

    public void onLoginButtonClick(){
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        retrofitAPI.loginUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == HttpURLConnection.HTTP_OK) {
                    user.setToken(response.headers().get("Authorization"));
                    userLiveData.setValue(user);
                    loginListener.onSuccess("Login successful", userLiveData);
                } else if(response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
                    loginListener.onFailure("Invalid password");
                } else if(response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    loginListener.onFailure("Invalid username");
                } else {
                    loginListener.onFailure("Please fill all fields");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loginListener.onNoConnection("No connection");
                Log.i("Login error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void onSignUpButtonClick(){
        loginListener.onSignUpButtonClick();
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }
}
