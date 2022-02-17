package de.hhn.aib.swlab.wise1920.group05.exercise3.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.SocketException;
import java.util.List;

import de.hhn.aib.swlab.wise1920.group05.exercise3.R;
import de.hhn.aib.swlab.wise1920.group05.exercise3.model.GameMessage;
import de.hhn.aib.swlab.wise1920.group05.exercise3.view.adapter.MyRecyclerAdapter;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.RoomListViewModel;
import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.listener.RoomListListener;

import de.hhn.aib.swlab.wise1920.group05.exercise3.network.MessageListener;
import de.hhn.aib.swlab.wise1920.group05.exercise3.network.WebsocketService;


public class RoomListActivity extends AppCompatActivity implements RoomListListener, MessageListener, MyLobbyClickListener {

    private RoomListViewModel roomListViewModel;
    private RecyclerView recyclerView;
    private MyRecyclerAdapter recyclerAdapter;
    private WebsocketService websocketService;
    private boolean serviceBound = false;
    private Gson gson;
    private SharedPreferences userPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        setContentView(R.layout.activity_room_list);
        roomListViewModel = new RoomListViewModel();
        roomListViewModel.setRoomListListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        Intent bindIntent = new Intent(this, WebsocketService.class);
        bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);

        recyclerAdapter = new MyRecyclerAdapter();
        recyclerAdapter.setMyLobbyClickListener(this);
        recyclerView.setAdapter(recyclerAdapter);

        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            GameMessage gameMessage = new GameMessage("GETGAMES");
            gameMessage.setAuthentication(userPref.getString("token", null));
            gameMessage.setGameId(userPref.getString("username", null));
            try {
                websocketService.sendMessage(gson.toJson(gameMessage));
            } catch (SocketException e) {
                Toast.makeText(RoomListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                unbindService(connection);
                Intent loginIntent = new Intent(RoomListActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        roomListViewModel.onCancelButtonClick();
    }

    @Override
    public void onCancelButtonClick() {
        Intent exitIntent = new Intent(RoomListActivity.this, MainActivity.class);
        startActivity(exitIntent);
        finish();
    }

    @Override
    public void onJoinButtonClick() {
        Intent joinIntent = new Intent(RoomListActivity.this, LobbyActivity.class);
        startActivity(joinIntent);
        websocketService.deregisterListener(RoomListActivity.this);
        finish();
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebsocketService.WebsocketServiceBinder binder = (WebsocketService.WebsocketServiceBinder) service;
            websocketService = binder.getService();
            serviceBound = true;
            websocketService.registerListener(RoomListActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten
            userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
            GameMessage gameMessage = new GameMessage("GETGAMES");
            gameMessage.setAuthentication(userPref.getString("token", null));
            gameMessage.setGameId(userPref.getString("username", null));
            try {
                websocketService.sendMessage(gson.toJson(gameMessage));
            } catch (SocketException e) {
                Toast.makeText(RoomListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(RoomListActivity.this, LoginActivity.class);
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
        GameMessage roomListMessage = gson.fromJson(message, GameMessage.class);
        if(roomListMessage.getAction().equals("GETGAMES")) {
            Log.e("RoomListMessage", message);
            List<String> games = roomListMessage.getGames();
            runOnUiThread(() -> {
                for (int i = 0; i < games.size(); i++) {
                    recyclerAdapter.addGames(games.get(i));
                }
            });
        }
    }

    @Override
    public void onLobbyClick(String lobbyName) {
        SharedPreferences userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        GameMessage gameMessage = new GameMessage("CREATEGAME");
        gameMessage.setAuthentication(userPref.getString("token", null));
        gameMessage.setGameId(lobbyName);
        try {
            websocketService.sendMessage(gson.toJson(gameMessage));
            roomListViewModel.onJoinButtonClick();
        } catch (SocketException e) {
            Toast.makeText(RoomListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            unbindService(connection);
            Intent loginIntent = new Intent(RoomListActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }
}
