package de.hhn.aib.swlab.wise1920.group05.exercise2.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import de.hhn.aib.swlab.wise1920.group05.exercise2.R;
import de.hhn.aib.swlab.wise1920.group05.exercise2.view.MainActivity;

public class NearbyNotificationReceiver extends BroadcastReceiver {

    private Context context;
    private static final String CHANNEL_ID = "channel_id_nearby";
    private static final String CHANNEL_NAME = "channel_name_nearby";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        String username = bundle.getString("username");
        int userCount = bundle.getInt("userCount");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannel();
        notificationManager.notify(userCount, addNotification(username).build());
    }

    private void createNotificationChannel() {
        int importance;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }
    }

    private NotificationCompat.Builder addNotification(String username) {
        Intent mapIntent = new Intent(context, MainActivity.class);
        PendingIntent mapPendingIntent = PendingIntent.getActivity(context, 0, mapIntent, 0);
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.walk_notification)
                .setContentText(username + " is near you.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.walk_notification, "Go to map", mapPendingIntent)
                .setAutoCancel(true);
    }
}
