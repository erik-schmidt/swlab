package de.hhn.aib.swlab.wise1920.group05.exercise1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class AlarmManagerReceiver extends BroadcastReceiver {

    private Context context;
    static MediaPlayer mediaPlayer;
    private AlarmRepository alarmRepository;
    private NotificationManager notificationManager;


    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        alarmRepository = new AlarmRepository(context);
        notificationManager = new NotificationManager(context);
        Bundle bundle = intent.getExtras();
        int alarmId = bundle.getInt("alarmId");
        alarmRepository = new AlarmRepository(context);
        if(alarmRepository.isAlarmActive(alarmId)) {
            setAlarmTone();
            Intent alarmIntent = new Intent(context, AlarmScreenActivity.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(alarmIntent);
            mediaPlayer.start();
            notificationManager.removeNotification(alarmId);
            alarmRepository.deactivateAlarm(alarmRepository.getAlarm(alarmId));
        }
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            Alarm alarm = alarmRepository.getLastAddedAlarm();
            notificationManager.addNotification(alarm.getId(), alarm);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
            calendar.set(Calendar.MINUTE, alarm.getMinute());

            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent ringIntent = new Intent(context, AlarmManagerReceiver.class);
            ringIntent.putExtra("alarmId", alarm.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getId(), ringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    pendingIntent);
        }
    }

    public void setAlarmTone(){
        String alarmtone = "alarmtones";

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getString(alarmtone, null) == null){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(alarmtone, "Mario");
            editor.apply();
            mediaPlayer = MediaPlayer.create(context, R.raw.mario);
        } else if(prefs.getString(alarmtone, null).equals("Mario")){
            mediaPlayer = MediaPlayer.create(context, R.raw.mario);
        }else if(prefs.getString(alarmtone, null).equals("Nuclear")){
            mediaPlayer = MediaPlayer.create(context, R.raw.nuclear);
        }else {
            mediaPlayer = MediaPlayer.create(context, R.raw.minecraft);
        }
    }
}
