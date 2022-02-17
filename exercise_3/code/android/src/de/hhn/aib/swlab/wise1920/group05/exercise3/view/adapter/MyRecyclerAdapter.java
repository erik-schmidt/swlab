package de.hhn.aib.swlab.wise1920.group05.exercise3.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hhn.aib.swlab.wise1920.group05.exercise3.R;
import de.hhn.aib.swlab.wise1920.group05.exercise3.view.MyLobbyClickListener;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    private final List<String> games;
    private MyLobbyClickListener myLobbyClickListener;

    class MyViewHolder extends RecyclerView.ViewHolder{
        final ViewGroup viewGroup;
        final TextView roomListUsernameTV;

        MyViewHolder(ViewGroup v) {
            super(v);
            viewGroup = v;
            roomListUsernameTV = viewGroup.findViewById(R.id.roomListUsernameTV);
        }
    }

    public MyRecyclerAdapter() {
        games = new ArrayList<>();
    }

    @Override
    public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.room_list_view, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final String lobbyName = games.get(position);
        holder.roomListUsernameTV.setText(lobbyName);
        holder.roomListUsernameTV.setOnClickListener(v -> myLobbyClickListener.onLobbyClick(games.get(position)));
    }

    @Override
    public int getItemCount(){
        return games.size();
    }


    public void addGames(String lobbyName) {
        games.add(lobbyName);
        notifyDataSetChanged();
    }

    public void setMyLobbyClickListener(MyLobbyClickListener myLobbyClickListener) {
        this.myLobbyClickListener = myLobbyClickListener;
    }
}
