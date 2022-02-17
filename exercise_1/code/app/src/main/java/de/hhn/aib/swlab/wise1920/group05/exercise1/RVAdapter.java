package de.hhn.aib.swlab.wise1920.group05.exercise1;

import android.icu.util.LocaleData;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {
        private List<Alarm> list;

    public RVAdapter(AlarmRepository alarmRepository) {
        list = alarmRepository.getAlarms();
        alarmRepository.getLiveDataAlarms().observeForever(new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                list = alarms;
                notifyDataSetChanged();
            }
        });
    }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            public ViewGroup viewGroup;
            private TextView tvDescription;
            private TextView alarmTime;
            private Switch activeAlarm;

            public MyViewHolder(ViewGroup v) {
                super(v);
                viewGroup = v;

                tvDescription = viewGroup.findViewById(R.id.alarmNameTextView);
                alarmTime = viewGroup.findViewById(R.id.alarmTimeTextView);
                activeAlarm = viewGroup.findViewById(R.id.switchActivateAlarm);
            }
        }

        @NonNull
        @Override
        public RVAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list_view, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tvDescription.setText(list.get(position).getName());
            Date date = new Date();
            boolean active;
            if (list.get(position).getActive() == 1){
                active = true;
            } else {
                active = false;
            }
            holder.activeAlarm.setChecked(active);
            holder.alarmTime.setText(list.get(position).getTime());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

}
