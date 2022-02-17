package de.hhn.aib.swlab.wise1920.group05.exercise3.viewModel.listener;

import androidx.lifecycle.LiveData;

import de.hhn.aib.swlab.wise1920.group05.exercise3.model.User;

public interface LoginListener {
    void onSuccess(String message, LiveData<User> userLiveData);
    void onFailure(String message);
    void onNoConnection(String message);
    void onSignUpButtonClick();
}
