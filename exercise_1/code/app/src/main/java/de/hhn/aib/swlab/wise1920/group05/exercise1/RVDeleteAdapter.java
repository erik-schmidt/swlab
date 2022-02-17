package de.hhn.aib.swlab.wise1920.group05.exercise1;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVDeleteAdapter extends RecyclerView.Adapter<RVDeleteAdapter.MyViewHolder> {
        private List<Alarm> list;

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            public ViewGroup viewGroup;
            private TextView tvDescription;
            private TextView alarmTime;

            public MyViewHolder(ViewGroup v) {
                super(v);
                viewGroup = v;

                tvDescription = viewGroup.findViewById(R.id.alarmNameDeleteTextView);
                alarmTime = viewGroup.findViewById(R.id.alarmTimeDeleteTextView);
            }
        }

        public RVDeleteAdapter(AlarmRepository alarmRepository) {
            list = alarmRepository.getAlarms();
        }

        @NonNull
        @Override
        public RVDeleteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_alarms_view, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tvDescription.setText(list.get(position).getName());
            holder.alarmTime.setText(list.get(position).getTime());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

}
