package de.hhn.aib.swlab.wise1920.group05.exercise1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

public class CreateAlarm extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        final View viewSaveButton = findViewById(R.id.btnSaveAlarm);
        final EditText alarmText = findViewById(R.id.alarmTextField);
        final TimePicker alarmTimePicker = findViewById(R.id.timePicker2);
        viewSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent saveNewAlarmIntent = new Intent();
                String alarmName = alarmText.getText().toString();
                saveNewAlarmIntent.putExtra("alarmName", alarmName);
                int alarmHour = alarmTimePicker.getHour();
                saveNewAlarmIntent.putExtra("alarmHour", alarmHour);
                int alarmMinute = alarmTimePicker.getMinute();
                saveNewAlarmIntent.putExtra("alarmMinute", alarmMinute);
                setResult(2345, saveNewAlarmIntent);
                finish();
            }
        });
    }


}
