package de.hhn.aib.swlab.wise1920.group05.exercise1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class AlarmScreenActivity extends AppCompatActivity {

    private AlarmRepository alarmRepository;

    public AlarmScreenActivity() {
        alarmRepository = new AlarmRepository(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        Calendar calendar = Calendar.getInstance();
        TextView timeText = findViewById(R.id.timeText);
        if(calendar.get(Calendar.MINUTE) < 10){
            timeText.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + 0 + calendar.get(Calendar.MINUTE));
        } else {
            timeText.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        }

        TextView alarmName = findViewById(R.id.alarmNameTextView);
        alarmName.setText("Alarm");


        FloatingActionButton deactivateAlarm = findViewById(R.id.deactivateAlarm);
        deactivateAlarm.setImageResource(R.drawable.ic_alarm_off_black_24dp);
        deactivateAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManagerReceiver.mediaPlayer.stop();
                finish();
            }
        });
    }

    //Empty method to prevent back-button press
    @Override
    public void onBackPressed(){

    }
}
