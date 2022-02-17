package de.hhn.aib.swlab.wise1920.group05.exercise3.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import de.hhn.aib.swlab.wise1920.group05.exercise3.R;
import de.hhn.aib.swlab.wise1920.group05.exercise3.model.User;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.LoginViewModel;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.listener.LoginListener;

public class LoginActivity extends AppCompatActivity implements LoginListener {

    private LoginViewModel loginViewModel;
    private SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new LoginViewModel();
        loginViewModel.setLoginListener(this);
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            EditText username = findViewById(R.id.loginUsernameEditText);
            EditText password = findViewById(R.id.loginPasswordEditText);
            loginViewModel.setUsername(username.getText().toString());
            loginViewModel.setPassword(password.getText().toString());
            loginViewModel.onLoginButtonClick();
        });
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(v -> loginViewModel.onSignUpButtonClick());
    }

    @Override
    public void onSuccess(String message, LiveData<User> userLiveData) {
        userLiveData.observe(this, user -> {
            userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPref.edit();
            userEditor.putString("username", user.getUsername());
            userEditor.putString("token", user.getToken());
            userEditor.apply();
        });
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoConnection(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignUpButtonClick() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }
}
