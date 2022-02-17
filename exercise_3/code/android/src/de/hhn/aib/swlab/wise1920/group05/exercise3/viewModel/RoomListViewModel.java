package de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel;

import androidx.lifecycle.ViewModel;

import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.listener.RoomListListener;

public class RoomListViewModel extends ViewModel {

    private RoomListListener roomListListener;

    public void onJoinButtonClick() {
        roomListListener.onJoinButtonClick();
    }

    public void onCancelButtonClick() {
        roomListListener.onCancelButtonClick();
    }

    public void setRoomListListener(RoomListListener roomListListener) {
        this.roomListListener = roomListListener;
    }
}
