package de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel;

import androidx.lifecycle.ViewModel;

import de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.listener.HomeListener;

public class HomeViewModel extends ViewModel {

    private HomeListener homeListener;

    public void onNewGameButtonClick() {
        homeListener.onNewGameButtonClick();
    }

    public void onJoinGameButtonClick() {
        homeListener.onJoinButtonClick();
    }

    public void setHomeListener(HomeListener homeListener) {
        this.homeListener = homeListener;
    }
}
