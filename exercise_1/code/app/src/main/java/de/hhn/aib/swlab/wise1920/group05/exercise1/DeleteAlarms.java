package de.hhn.aib.swlab.wise1920.group05.exercise1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class DeleteAlarms extends AppCompatActivity {

    private RecyclerView rvDeleteAlarms;
    private AlarmRepository alarmRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_alarms);

        alarmRepository = new AlarmRepository(getApplicationContext());
        initialiseRecyclerView();

        final View viewDeleteButton = findViewById(R.id.btnDeleteAlarms);
        viewDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent deleteAlarmsIntent = new Intent();
                boolean[] deleteAlarmsList = new boolean[rvDeleteAlarms.getAdapter().getItemCount()];
                for (int i = 0; i <= rvDeleteAlarms.getAdapter().getItemCount()-1; i++){
                    CheckBox  cbx = rvDeleteAlarms.getChildAt(i).findViewById(R.id.deleteCheckBox);
                    deleteAlarmsList[i] = cbx.isChecked();
                }
                deleteAlarmsIntent.putExtra("DeleteAlarmsList", deleteAlarmsList);
                setResult(3537, deleteAlarmsIntent);
                finish();
            }
        });
    }

    private void initialiseRecyclerView(){
        rvDeleteAlarms = findViewById(R.id.rvDeleteAlarmList);
        rvDeleteAlarms.setAdapter(new RVDeleteAdapter(alarmRepository));
        rvDeleteAlarms.setLayoutManager(new LinearLayoutManager(this));
    }
}
