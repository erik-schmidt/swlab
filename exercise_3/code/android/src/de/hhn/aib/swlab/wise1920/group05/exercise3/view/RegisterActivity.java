package de.hhn.aib.swlab.wise1920.group05.exercise3.view;

import androidx.lifecycle.LiveData;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.SocketException;

import de.hhn.aib.swlab.wise1920.group05.exercise3.R;
import de.hhn.aib.swlab.wise1920.group05.exercise3.model.GameMessage;
import de.hhn.aib.swlab.wise1920.group05.exercise3.model.User;
import de.hhn.aib.swlab.wise1920.group05.exercise3.network.WebsocketService;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.listener.RegisterListener;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity implements RegisterListener {

    private RegisterViewModel registerViewModel;
    private SharedPreferences userPref;
    private WebsocketService websocketService;
    private boolean serviceBound = false;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerViewModel = new RegisterViewModel();
        registerViewModel.setRegisterListener(this);
        gson = new Gson();

        Intent bindIntent = new Intent(this, WebsocketService.class);
        bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);

        Button cancelButton = findViewById(R.id.cancelRegisterButton);
        cancelButton.setOnClickListener(v -> registerViewModel.onCancelButtonClick());

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            EditText username = findViewById(R.id.registerUsernameEditText);
            registerViewModel.setUsername(username.getText().toString());
            EditText password = findViewById(R.id.registerPasswordEditText);
            registerViewModel.setPassword(password.getText().toString());
            registerViewModel.onRegisterButtonClick();
        });
    }


    @Override
    public void onSuccess(String message, LiveData<User> userLiveData) {
        userLiveData.observe(this, user -> {
            userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPref.edit();
            userEditor.putString("username", user.getUsername());
            userEditor.apply();
        });
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        GameMessage gameMessage = new GameMessage("LOGIN");
        gameMessage.setGameId(userPref.getString("username", null));
        gameMessage.setAuthentication(userPref.getString("token", null));
        try {
            websocketService.sendMessage(gson.toJson(gameMessage));
            Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(registerIntent);
            finish();
        } catch (SocketException e) {
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            unbindService(connection);
            Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
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
    public void onCancel() {
        Intent cancelIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(cancelIntent);
        finish();
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebsocketService.WebsocketServiceBinder binder = (WebsocketService.WebsocketServiceBinder) service;
            websocketService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

}
