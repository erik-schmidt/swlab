package de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.net.HttpURLConnection;

import de.hhn.aib.swlab.wise1920.group05.exercise2.model.User;
import de.hhn.aib.swlab.wise1920.group05.exercise2.network.RetrofitAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginViewModel extends ViewModel {

    private String username;
    private String password;
    private final RetrofitAPI retrofitAPI;
    private LoginListener loginListener;
    private final MutableLiveData<User> userLiveData;

    public LoginViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
        userLiveData = new MutableLiveData<>();
    }

    public void onLoginButtonClick(View view){
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        retrofitAPI.loginUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    user.setToken(response.headers().get("Authorization"));
                    assert response.body() != null;
                    user.setId(response.body().getId());
                    user.setDescription(response.body().getDescription());
                    userLiveData.setValue(user);
                    loginListener.onSuccess("Login successful", userLiveData);
                } else if (response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
                    loginListener.onFailure("Invalid password");
                } else if(response.code() == HttpURLConnection.HTTP_NOT_FOUND){
                    loginListener.onFailure("Invalid username");
                } else {
                    loginListener.onFailure("Please fill all fields.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loginListener.onNoConnection("No Connection");
            }
        });
    }

    public void onSignUpButtonClick(View view){
        loginListener.onSignUp();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }
}
