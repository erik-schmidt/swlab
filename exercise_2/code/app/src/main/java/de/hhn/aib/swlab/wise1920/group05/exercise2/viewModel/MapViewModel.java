package de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel;

import android.location.Location;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.net.HttpURLConnection;
import java.util.List;
import de.hhn.aib.swlab.wise1920.group05.exercise2.R;
import de.hhn.aib.swlab.wise1920.group05.exercise2.model.MyLocation;
import de.hhn.aib.swlab.wise1920.group05.exercise2.model.OtherUser;
import de.hhn.aib.swlab.wise1920.group05.exercise2.network.RetrofitAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapViewModel extends ViewModel {

    private final RetrofitAPI retrofitAPI;
    private MyMapListener mapListener;
    private final MutableLiveData<List<OtherUser>> userLiveDataList;
    private final MutableLiveData<List<MyLocation>> userLocationList;

    private String userID;
    private String userJWT;
    private int userRadius;
    private Location currentLocation;

    public MapViewModel(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
        userLiveDataList = new MutableLiveData<>();
        userLocationList = new MutableLiveData<>();
    }

    public void sendLocationToWebservice(){
        retrofitAPI.updateLocation(userJWT,userID, currentLocation).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == HttpURLConnection.HTTP_OK) {
                    Log.i("UpdateLocation: ", "Successful, " + "UserLocation: " + getLocation().getLatitude() + " " + getLocation().getLongitude());
                }
                else if(response.code() == HttpURLConnection.HTTP_FORBIDDEN){
                    mapListener.onFailure("UpdateLocation: Invalid JWT");
                }
                else {
                    mapListener.onFailure("Uh, something went wrong");
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mapListener.onNoConnection("Update Location: No Connection");
            }
        });
    }

    public void initNearbyUser(){
        retrofitAPI.getLocations(userJWT, userRadius, currentLocation.getLatitude(), currentLocation.getLongitude()).enqueue(new Callback<List<OtherUser>>() {

            @Override
            public void onResponse(Call<List<OtherUser>> call, Response<List<OtherUser>> response) {
                if(response.code() == HttpURLConnection.HTTP_OK) {
                    userLiveDataList.postValue(response.body());
                    mapListener.onSuccess(userLiveDataList);
                }
                else if(response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
                    mapListener.onTokenExpired("getLocations: Invalid Token");
                }
                else {
                    mapListener.onFailure("Uh, something went wrong");
                }
            }
            @Override
            public void onFailure(Call<List<OtherUser>> call, Throwable t) {
                mapListener.onNoConnection("NearbyUser: No Connection");
            }
        });
    }

    public void setLocationHistory(){
        retrofitAPI.getLocations(userJWT, userID).enqueue(new Callback<List<MyLocation>>() {
            @Override
            public void onResponse(Call<List<MyLocation>> call, Response<List<MyLocation>> response) {
                if(response.code() == HttpURLConnection.HTTP_OK){
                    userLocationList.setValue(response.body());
                    mapListener.onLocationHistoryChecked(userLocationList);
                } else if(response.code() == HttpURLConnection.HTTP_FORBIDDEN){
                    mapListener.onTokenExpired("Your token expired, please login again");
                } else {
                    mapListener.onFailure("Uh, something went wrong");
                }
            }

            @Override
            public void onFailure(Call<List<MyLocation>> call, Throwable t) {
                mapListener.onNoConnection("No connection");
            }
        });
    }

    public OnMenuItemClickListener toolbarOnItemClickListener(){
        return item -> {
            if (item.getItemId() == R.id.profileMenuItem){
                mapListener.profileClick();
                return true;
            } else if (item.getItemId() == R.id.settingsMenuItem){
                mapListener.settingsClick();
                return true;
            } else if (item.getItemId() == R.id.locationHistoryMenuItem){
                if(item.isChecked()){
                    item.setChecked(false);
                    mapListener.onLocationHistoryUnchecked();
                } else {
                    item.setChecked(true);
                    setLocationHistory();
                }
                return true;
            }
            else if(item.getItemId() == R.id.logoutMenuItem){
                mapListener.onLogoutClick();
                return true;
            }
            return false;
        };
    }

    public void onBackToLocatinButtonClick(View view){
        mapListener.onBackToLocationClick();
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserJWT() {
        return userJWT;
    }

    public void setUserJWT(String userJWT) {
        this.userJWT = userJWT;
    }

    public int getUserRadius() {
        return userRadius;
    }

    public void setUserRadius(int userRadius) {
        this.userRadius = userRadius;
    }

    public void setMyMapListener(MyMapListener myMapListener){
        this.mapListener = myMapListener;
    }

    public Location getLocation() {
        return currentLocation;
    }

    public void setLocation(Location location) {
        this.currentLocation = location;
    }

}
