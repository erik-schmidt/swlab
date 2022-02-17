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

public class RegisterViewModel extends ViewModel {

    private String username;
    private String password;
    private String description;
    private final RetrofitAPI retrofitAPI;
    private RegisterListener registerListener;
    private final MutableLiveData<User> userLiveDate;

    public RegisterViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
        userLiveDate = new MutableLiveData<>();
    }

    public void onRegisterButtonClick(View view){
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setDescription(description);
        retrofitAPI.registerUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == HttpURLConnection.HTTP_OK){
                    loginUser(user);
                } else if(response.code() == HttpURLConnection.HTTP_CONFLICT){
                    registerListener.onFailure("Username already taken");
                } else {
                    Log.i("ResponseCode", " " + response.code());
                    registerListener.onFailure("Please fill all fields.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                registerListener.onNoConnection("No Connection");
            }
        });
    }

    private void loginUser(final User user){
        retrofitAPI.loginUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code() == HttpURLConnection.HTTP_OK){
                    user.setToken(response.headers().get("Authorization"));
                    assert response.body() != null;
                    user.setId(response.body().getId());
                    user.setDescription(response.body().getDescription());
                    userLiveDate.setValue(user);
                    registerListener.onSuccess("Registration successful", userLiveDate);
                } else if(response.code() == HttpURLConnection.HTTP_FORBIDDEN){
                    registerListener.onFailure("Invalid username/password");
                } else {
                    registerListener.onFailure("Login failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                registerListener.onNoConnection("No Connection");
            }
        });
    }

    public void onCancelButtonClick(View view){
        registerListener.onCancel();
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
        if(description == null){
            description = "";
        }
        this.description = description;
    }

    public void setRegisterListener(RegisterListener registerListener) {
        this.registerListener = registerListener;
    }
}
