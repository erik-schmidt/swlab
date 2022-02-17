package de.hhn.aib.swlab.wise1920.group05.exercise3.network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebsocketService extends Service {

    private static final String URL = "wss://g5-master.stud.ex3.swlab-hhn.de/ws";
    private final IBinder binder = new WebsocketServiceBinder();
    private OkHttpClient client;
    private WebSocket webSocket;
    private List<MessageListener> listeners;

    public class WebsocketServiceBinder extends Binder {
        public WebsocketService getService() {
            return WebsocketService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (webSocket == null) {
            Request request = new Request.Builder().url(URL).build();
            webSocket = client.newWebSocket(request, new SocketListener());
            Log.e("Service", "onBind called");
        }
        return binder;
    }

    @Override
    public void onCreate() {
        listeners = new ArrayList<>();
        client = new OkHttpClient();
        Log.e("Service", "onCreate executed");
    }

    private final class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket socket, Response response) {
            Log.e(WebsocketService.this.getClass().getSimpleName(), "Socket opened");
        }

        @Override
        public void onMessage(WebSocket socket, String text) {
            Log.i(WebsocketService.this.getClass().getSimpleName(), "message received: " + text);
            for (MessageListener listener : listeners) {
                listener.onMessageReceived(text);
            }
        }

        @Override
        public void onMessage(@NonNull WebSocket socket, ByteString byteString) {
            Log.e(WebsocketService.this.getClass().getSimpleName(), "Received bytes: " + byteString.hex());
        }

        @Override
        public void onClosing(WebSocket socket, int code, @NonNull String reason) {
            webSocket.close(1000, null);
            WebsocketService.this.webSocket = null;
            Log.e(WebsocketService.this.getClass().getSimpleName(), "closing: " + reason);
        }

        @Override
        public void onClosed(@NonNull WebSocket socket, int code, @NonNull String reason) {
            Log.e(WebsocketService.this.getClass().getSimpleName(), "closed: " + reason);
        }

        @Override
        public void onFailure(@NonNull WebSocket socket, Throwable t, Response response) {
            Log.e(WebsocketService.this.getClass().getSimpleName(), "Error: " + t.getMessage());
            t.printStackTrace();
        }
    }

    public void sendMessage(String message) throws SocketException {
        boolean status = webSocket.send(message);
        if (status) {
            Log.i(this.getClass().getSimpleName(), "Send message " + message + ", status: " + true);
        } else {
            throw new SocketException("No Connection to the Server!");
        }
    }

    public void registerListener(MessageListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void deregisterListener(MessageListener listener) {
        listeners.remove(listener);
    }
}
