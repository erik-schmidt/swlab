package de.hhn.aib.swlab.wise1920.group05.exercise3.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.net.SocketException;

import de.hhn.aib.swlab.wise1920.group05.exercise3.R;
import de.hhn.aib.swlab.wise1920.group05.exercise3.model.GameMessage;
import de.hhn.aib.swlab.wise1920.group05.exercise3.network.WebsocketService;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.HomeViewModel;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.listener.HomeListener;

public class MainActivity extends AppCompatActivity implements HomeListener {

    private HomeViewModel homeViewModel;
    private WebsocketService websocketService;
    private boolean serviceBound = false;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeViewModel = new HomeViewModel();
        homeViewModel.setHomeListener(this);
        Intent bindIntent = new Intent(this, WebsocketService.class);
        bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
        Button newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(v -> {
            GameMessage gameMessage = new GameMessage("CREATEGAME");
            SharedPreferences userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
            gameMessage.setAuthentication(userPref.getString("token", null));
            gameMessage.setGameId(userPref.getString("username", null));
            try {
                websocketService.sendMessage(gson.toJson(gameMessage));
                homeViewModel.onNewGameButtonClick();
            } catch (SocketException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                unbindService(connection);
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
        Button joinGameButton = findViewById(R.id.joinGameButton);
        joinGameButton.setOnClickListener(v -> homeViewModel.onJoinGameButtonClick());
    }


    @Override
    public void onNewGameButtonClick() {
        Intent playerRoomIntent = new Intent(MainActivity.this, LobbyActivity.class);
        startActivity(playerRoomIntent);
        finish();
    }

    @Override
    public void onJoinButtonClick() {
        Intent roomListIntent = new Intent(MainActivity.this, RoomListActivity.class);
        startActivity(roomListIntent);
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebsocketService.WebsocketServiceBinder binder = (WebsocketService.WebsocketServiceBinder) service;
            websocketService = binder.getService();
            serviceBound = true;
            SharedPreferences userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
            GameMessage gameMessage = new GameMessage("LOGIN");
            gameMessage.setGameId(userPref.getString("username", null));
            gameMessage.setAuthentication(userPref.getString("token", null));
            try {
                websocketService.sendMessage(gson.toJson(gameMessage));
            } catch (SocketException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                unbindService(connection);
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
}
