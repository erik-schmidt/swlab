package de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.hhn.aib.swlab.wise1920.group05.exercise2.model.MyLocation;
import de.hhn.aib.swlab.wise1920.group05.exercise2.model.OtherUser;

public interface MyMapListener {

    void onSuccess(LiveData<List<OtherUser>> userLiveData);
    void profileClick();
    void settingsClick();
    void onLogoutClick();
    void onTokenExpired(String message);
    void onLocationHistoryChecked(LiveData<List<MyLocation>> userLocationList);
    void onLocationHistoryUnchecked();
    void onFailure(String message);
    void onNoConnection(String message);
    void onBackToLocationClick();

}
