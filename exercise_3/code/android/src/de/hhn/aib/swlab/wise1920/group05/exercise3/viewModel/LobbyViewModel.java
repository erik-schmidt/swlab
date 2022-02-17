package de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel;

import androidx.lifecycle.ViewModel;

import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.listener.LobbyListener;

public class LobbyViewModel extends ViewModel {

    private LobbyListener lobbyListener;

    public void onCancelClick(){
        lobbyListener.onCancelClick();
    }

    public void onStartClick(){
        lobbyListener.onStartClick();
    }

    public void setLobbyListener(LobbyListener lobbyListener) {
        this.lobbyListener = lobbyListener;
    }
}
