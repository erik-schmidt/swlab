package de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel;


public interface WebserviceListener {

    void onSuccess(String message);
    void onFailure(String message);
    void onNoConnection(String message);
    void onTokenExpired(String message);
}
