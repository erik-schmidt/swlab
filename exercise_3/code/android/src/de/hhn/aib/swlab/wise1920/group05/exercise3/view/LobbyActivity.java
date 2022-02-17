package de.hhn.aib.swlab.wise1920.group05.exercise3.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;

import java.net.SocketException;

import de.hhn.aib.swlab.wise1920.group05.exercise3.R;
import de.hhn.aib.swlab.wise1920.group05.exercise3.model.GameMessage;
import de.hhn.aib.swlab.wise1920.group05.exercise3.network.MessageListener;
import de.hhn.aib.swlab.wise1920.group05.exercise3.network.WebsocketService;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.LobbyViewModel;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.listener.LobbyListener;

public class LobbyActivity extends AppCompatActivity implements LobbyListener, MessageListener {

    private LobbyViewModel lobbyViewModel;
    private boolean serviceBound = false;
    private WebsocketService websocketService;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_room);
        lobbyViewModel = new LobbyViewModel();
        lobbyViewModel.setLobbyListener(this);
        gson = new Gson();

        Intent serviceIntent = new Intent(this,WebsocketService.class);
        bindService(serviceIntent,connection, Context.BIND_AUTO_CREATE);

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> lobbyViewModel.onCancelClick());
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> lobbyViewModel.onStartClick());

        SharedPreferences userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        TextView playerNameText = findViewById(R.id.playerNameText);
        playerNameText.setText(userPref.getString("username", null));
    }


    @Override
    public void onCancelClick() {
        GameMessage leaveMessage = new GameMessage();
        leaveMessage.setMessageType(GameMessage.MessageType.LEAVE_LOBBY);
        try {
            websocketService.sendMessage(gson.toJson(leaveMessage));
            Intent exitIntent = new Intent(LobbyActivity.this, MainActivity.class);
            startActivity(exitIntent);
            finish();
        } catch (SocketException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            unbindService(connection);
            Intent loginIntent = new Intent(LobbyActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    public void onStartClick() {
        GameMessage startMessage = new GameMessage();
        startMessage.setMessageType(GameMessage.MessageType.START_GAME);
        Toast.makeText(LobbyActivity.this, "Start message send to Opponent", Toast.LENGTH_SHORT).show();
        try {
            websocketService.sendMessage(gson.toJson(startMessage));
        } catch (SocketException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            unbindService(connection);
            Intent loginIntent = new Intent(LobbyActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebsocketService.WebsocketServiceBinder binder = (WebsocketService.WebsocketServiceBinder) service;
            websocketService = binder.getService();
            serviceBound = true;
            websocketService.registerListener(LobbyActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten
            SharedPreferences userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
            GameMessage gameMessage = new GameMessage();
            gameMessage.setMessageType(GameMessage.MessageType.JOINED);
            gameMessage.setPlayerName(userPref.getString("username", null));
            try {
                websocketService.sendMessage(gson.toJson(gameMessage));
            } catch (SocketException e) {
                Toast.makeText(LobbyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                unbindService(connection);
                Intent loginIntent = new Intent(LobbyActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };


    @Override
    public void onMessageReceived(String message) {
        ProgressBar waitingForPlayerPB = findViewById(R.id.waitingForPlayerPB);
        GameMessage joinMessage = gson.fromJson(message, GameMessage.class);
        TextView opponentTextView = findViewById(R.id.waitingForPlayerText);
        if (joinMessage.getMessageType() == GameMessage.MessageType.JOINED) {
            runOnUiThread(() -> {
                opponentTextView.setText(joinMessage.getPlayerName());
                waitingForPlayerPB.setVisibility(View.INVISIBLE);
                Toast.makeText(LobbyActivity.this, joinMessage.getPlayerName() + " joined your game", Toast.LENGTH_SHORT).show();
            });
            if(!opponentTextView.getText().toString().equals(joinMessage.getPlayerName())) {
                GameMessage joinedMessage = new GameMessage();
                SharedPreferences userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
                joinedMessage.setMessageType(GameMessage.MessageType.JOINED);
                joinedMessage.setPlayerName(userPref.getString("username", null));
                try {
                    websocketService.sendMessage(gson.toJson(joinedMessage));
                } catch (SocketException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    unbindService(connection);
                    Intent loginIntent = new Intent(LobbyActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            }
        }
        if(joinMessage.getMessageType() == GameMessage.MessageType.START_GAME) {
            Intent startIntent = new Intent(LobbyActivity.this, GameActivity.class);
            startActivity(startIntent);
            finish();
        }
        if (joinMessage.getMessageType() == GameMessage.MessageType.ERROR) {
            runOnUiThread(() -> Toast.makeText(LobbyActivity.this, "No Player in Lobby", Toast.LENGTH_SHORT).show());
        }
        if (joinMessage.getMessageType() == GameMessage.MessageType.LEAVE_LOBBY) {
            runOnUiThread(() -> {
                Toast.makeText(LobbyActivity.this, "Opponent left the lobby", Toast.LENGTH_SHORT).show();
                waitingForPlayerPB.setVisibility(View.VISIBLE);
                opponentTextView.setText(R.string.waiting_for_player);
            });
            SharedPreferences userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
            GameMessage createGameMessage = new GameMessage();
            createGameMessage.setAction("CREATEGAME");
            createGameMessage.setGameId(userPref.getString("username", null));
            try {
                websocketService.sendMessage(gson.toJson(createGameMessage));
            } catch (SocketException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                unbindService(connection);
                Intent loginIntent = new Intent(LobbyActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }
    }
}
