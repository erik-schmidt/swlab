package de.hhn.aib.swlab.wise1920.group05.exercise2.view;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;
import java.util.Objects;

import de.hhn.aib.swlab.wise1920.group05.exercise2.R;
import de.hhn.aib.swlab.wise1920.group05.exercise2.databinding.ActivityMainBinding;
import de.hhn.aib.swlab.wise1920.group05.exercise2.model.MyLocation;
import de.hhn.aib.swlab.wise1920.group05.exercise2.model.OtherUser;
import de.hhn.aib.swlab.wise1920.group05.exercise2.model.NearbyNotificationReceiver;
import de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel.MapViewModel;
import de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel.MyMapListener;

public class MainActivity extends AppCompatActivity implements MyMapListener, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private MapView myMapView;
    private MapController myMapController;
    private LocationManager locationManager;
    private MapViewModel mapViewModel;
    private MyLocationNewOverlay myLocationNewOverlay;
    private SharedPreferences userPref;
    private SharedPreferences sharedPref;
    private Handler timerHandler;

    public static final int MY_PERMISSION_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 98;
    public static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 97;
    private static final int DEFAULT_ZOOM_VALUE = 18;
    private static int counter = 0;

    private Location lastLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapViewModel = ViewModelProviders.of(MainActivity.this).get(MapViewModel.class);
        mapViewModel.setMyMapListener(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        binding.setMapViewModel(mapViewModel);

        myMapView = findViewById(R.id.mapView);
        initMapViewSettings();

        userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        initSharedPreferenceVar();


        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), myMapView);
        myMapView.getOverlays().add(myLocationNewOverlay);

        myMapController = (MapController) myMapView.getController();

        initMapOverlayAndMapControllerSettings();

        Toolbar toolbar = findViewById(R.id.mapViewToolbar);
        toolbar.setOnMenuItemClickListener(mapViewModel.toolbarOnItemClickListener());

        mapViewModel.initNearbyUser();
        mapViewModel.sendLocationToWebservice();
        timer(sharedPref.getInt("updateInterval", 5));
    }

    private void initMapViewSettings() {
        myMapView.setTileSource(TileSourceFactory.MAPNIK);
        myMapView.setMultiTouchControls(true);
        if (locationManager != null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_LOCATION);
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_REQUEST_LOCATION);
                return;
            }
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        mapViewModel.setLocation(lastLocation);
    }

    private void initSharedPreferenceVar() {
        mapViewModel.setUserJWT(userPref.getString("token", null));
        mapViewModel.setUserID(userPref.getString("id", null));
        mapViewModel.setUserRadius(sharedPref.getInt("radius", 5));
    }

    private void initMapOverlayAndMapControllerSettings() {
        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.enableFollowLocation();
        myMapController.setZoom(DEFAULT_ZOOM_VALUE);
    }

    /**
     * Timer zum Updaten der Marker
     */
    private void timer(int seconds) {
        timerHandler = new Handler();
        timerHandler.postDelayed(() -> {
            if (mapViewModel.getLocation() == null) {
                Toast.makeText(MainActivity.this, "Timer: No Connection", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Location updated", Toast.LENGTH_SHORT).show();
                mapViewModel.sendLocationToWebservice();
                mapViewModel.initNearbyUser();
            }
            int interval = PreferenceManager.getDefaultSharedPreferences(this).getInt("updateInterval", 5);
            timer(interval);
        }, 1000 * seconds);
    }

    @Override
    public void onResume() {
        super.onResume();
        myMapView.invalidate();
        myMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        myMapView.getOverlays().clear();
        myMapView.onPause();
    }

    @Override
    public void onSuccess(LiveData<List<OtherUser>> userLiveData) {
        myMapView.getOverlays().clear();
        myMapView.getOverlays().add(myLocationNewOverlay);
        if(userPref.getBoolean("locationHistory", false)){
            mapViewModel.setLocationHistory();
        }
        userLiveData.observeForever(users -> {
            if(!users.isEmpty()) {
                for(OtherUser user : users) {
                    createNotificationReceiver(user, counter);
                    counter++;
                    Marker marker = new Marker(myMapView);
                    marker.setPosition(new GeoPoint(user.getLocation().getLatitude(), user.getLocation().getLongitude()));
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle(user.getName());
                    marker.setSubDescription(user.getDescription());
                    myMapView.getOverlays().add(marker);
                }
            }
            myMapView.invalidate();
        });
    }

    @Override
    public void profileClick() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    @Override
    public void settingsClick() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public void onLocationHistoryChecked(LiveData<List<MyLocation>> userLocationList){
        SharedPreferences.Editor userPrefEditor = userPref.edit();
        userPrefEditor.putBoolean("locationHistory", true);
        userPrefEditor.apply();
        userLocationList.observeForever(myLocations -> {
            for(MyLocation myLocation : myLocations){
                Marker marker = new Marker(myMapView);
                double userLatitude = myLocation.getLatitude();
                double userLongitude = myLocation.getLongitude();
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.person, null));
                marker.setPosition(new GeoPoint(userLatitude, userLongitude));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                myMapView.getOverlays().add(marker);
            }
        });
    }

    @Override
    public void onLocationHistoryUnchecked(){
        SharedPreferences.Editor userPrefEditor = userPref.edit();
        userPrefEditor.putBoolean("locationHistory", false);
        userPrefEditor.apply();
        myMapView.getOverlays().clear();
        myMapView.getOverlays().add(myLocationNewOverlay);
        mapViewModel.initNearbyUser();
    }

    @Override
    public void onNoConnection(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackToLocationClick() {
        myMapController.setZoom(DEFAULT_ZOOM_VALUE);
        myMapController.setCenter(myLocationNewOverlay.getMyLocation());
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTokenExpired(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        stopTimer();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "AccessFineLocation Granted", Toast.LENGTH_SHORT).show();
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    }
                }
                break;
            case MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "WriteStorage Granted", Toast.LENGTH_SHORT).show();
                    }
                }
            case MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "ReadStorage Granted", Toast.LENGTH_SHORT).show();
                    }
                }
        }

    }

    private void createNotificationReceiver(OtherUser otherUser, int userCount) {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, NearbyNotificationReceiver.class);
        notificationIntent.putExtra("username", otherUser.getName());
        notificationIntent.putExtra("userCount", userCount);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        ComponentName receiver = new ComponentName(this, NearbyNotificationReceiver.class);
        PackageManager packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, sharedPref.getInt("updateInterval", 5), pendingIntent);
    }

    @Override
    public void onLogoutClick(){
        Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(logoutIntent);
        stopTimer();
        finish();
    }

    public void stopTimer(){
        timerHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onLocationChanged(Location location) {
        mapViewModel.setLocation(location);
        myMapView.invalidate();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }


}