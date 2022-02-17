package de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel;

import androidx.lifecycle.LiveData;

import de.hhn.aib.swlab.wise1920.group05.exercise2.model.User;

public interface RegisterListener {

    void onSuccess(String message, LiveData<User> userLiveData);
    void onFailure(String message);
    void onNoConnection(String message);
    void onCancel();
}
