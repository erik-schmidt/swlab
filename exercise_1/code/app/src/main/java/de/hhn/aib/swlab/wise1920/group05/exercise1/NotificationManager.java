package de.hhn.aib.swlab.wise1920.group05.exercise1;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationManager {

    private static final String CHANNEL_ID = "channel_id_alarmClock";              //Beispielvergabe der Channel ID
    private static final String CHANNEL_NAME = "channel_name_alarmClock";          //Beispielvergabe des Channel Namens
    private Context context;

    public NotificationManager(Context context) {
        this.context = context;
    }

    public void addNotification(int notificationID, Alarm alarm){
        //Builds the Notificationsettings
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Alarm: " + alarm.getName())
                .setContentText("Klingelt um: " + alarm.getTime())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)      //Needed for Android 7.1 and lower
                .setOngoing(true);

        //Creates the intent needed to show the notification
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 9999, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        //Add notification to notification manager
        android.app.NotificationManager manager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationID, builder.build());
    }

    //Creates the NotificationChannel. Only necessary on API 26+
    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {        //Check the API
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel alarmClockNotificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            alarmClockNotificationChannel.enableLights(true);
            alarmClockNotificationChannel.enableVibration(true);
            alarmClockNotificationChannel.setLightColor(Color.YELLOW);
            //Registering the channel with the system
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(alarmClockNotificationChannel);
        }
    }

    public void removeNotification(int notificationID){
        android.app.NotificationManager notificationManager = (android.app.NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);
    }
}
