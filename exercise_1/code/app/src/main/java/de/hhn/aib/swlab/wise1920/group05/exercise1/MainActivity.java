package de.hhn.aib.swlab.wise1920.group05.exercise1;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.List;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    private RecyclerView rvAlarms;

    private AlarmRepository alarmRepository;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        notificationManager = new NotificationManager(this);

        alarmRepository = new AlarmRepository(getApplicationContext());
        initialiseRecyclerView();
        notificationManager.createNotificationChannel();    //Creates a Notification Channel to register single Notifications

        //Check for active Alarms and add notifications
        List<Alarm> activeAlarms = alarmRepository.getActiveAlarms();
        if (activeAlarms.size() != 0){
            for (Alarm alarm : activeAlarms){
                notificationManager.addNotification(alarm.getId(), alarm);
            }
        }


        FloatingActionButton buttonCreateAlarm = findViewById(R.id.button_createAlarm);
        buttonCreateAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createNewAlarmIntent = new Intent(MainActivity.this, CreateAlarm.class);
                startActivityForResult(createNewAlarmIntent, 2345);
            }
        });

        //Add Oberserver for der Switches in the RecyclerView to Activate or Deactivate an Alarm
        rvAlarms.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                for (int i = 0; i <= rvAlarms.getAdapter().getItemCount()-1; i++){
                    Switch  activateSwitch = rvAlarms.getChildAt(i).findViewById(R.id.switchActivateAlarm);
                    activateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            List<Alarm> currentAlarmList = alarmRepository.getAlarms();
                            for (int i = 0; i <= rvAlarms.getAdapter().getItemCount()-1; i++){
                                Switch  swt = rvAlarms.getChildAt(i).findViewById(R.id.switchActivateAlarm);
                                if(!((swt.isChecked() && currentAlarmList.get(i).getActive() == 1) || (!swt.isChecked() && currentAlarmList.get(i).getActive() == 0))){
                                    if (swt.isChecked()){
                                        alarmRepository.activateAlarm(currentAlarmList.get(i));
                                        notificationManager.addNotification(currentAlarmList.get(i).getId(), currentAlarmList.get(i));
                                    } else {
                                        alarmRepository.deactivateAlarm(currentAlarmList.get(i));
                                        notificationManager.removeNotification(currentAlarmList.get(i).getId());
                                    }
                                }
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2345 && data != null) {
            final String alarmName = data.getStringExtra("alarmName");
            final int alarmHour = data.getIntExtra("alarmHour", 0);
            final int alarmMinute = data.getIntExtra("alarmMinute", 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
            calendar.set(Calendar.MINUTE, alarmMinute);

            alarmRepository.addAlarm(alarmName, alarmHour, alarmMinute);

            int alarmId = alarmRepository.getLastAddedAlarm().getId();
            AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
            Intent ringIntent = new Intent(this, AlarmManagerReceiver.class);
            ringIntent.putExtra("alarmId", alarmId);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, alarmId, ringIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            ComponentName componentName = new ComponentName(this, AlarmManagerReceiver.class);
            PackageManager packageManager = getPackageManager();
            packageManager.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, // or  COMPONENT_ENABLED_STATE_DISABLED
                    PackageManager.DONT_KILL_APP);

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    alarmIntent);
            initialiseRecyclerView();
            notificationManager.addNotification(alarmId, alarmRepository.getLastAddedAlarm());
        } else if (requestCode == 3537 && data != null){
            boolean[] deleteAlarmsList = data.getBooleanArrayExtra("DeleteAlarmsList");
            List<Alarm> oldAlarmList = alarmRepository.getAlarms();
            for (int i = 0; i <= deleteAlarmsList.length-1; i++){
                if (deleteAlarmsList[i]){
                    alarmRepository.deleteAlarm(oldAlarmList.get(i));
                    notificationManager.removeNotification(oldAlarmList.get(i).getId());
                }
            }
            initialiseRecyclerView();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsMenu) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.deleteAlarmsItem) {
            Intent deleteAlarmsIntent = new Intent(MainActivity.this, DeleteAlarms.class);
            startActivityForResult(deleteAlarmsIntent, 3537);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialiseRecyclerView(){
        rvAlarms = findViewById(R.id.rvAlarmList);
        rvAlarms.setAdapter(new RVAdapter(alarmRepository));
        rvAlarms.setLayoutManager(new LinearLayoutManager(this));
    }
}