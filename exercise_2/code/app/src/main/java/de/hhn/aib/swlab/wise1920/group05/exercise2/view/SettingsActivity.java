package de.hhn.aib.swlab.wise1920.group05.exercise2.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;


import de.hhn.aib.swlab.wise1920.group05.exercise2.R;
import de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel.WebserviceListener;

public class SettingsActivity extends AppCompatActivity implements WebserviceListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent settingsIntent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(settingsIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoConnection(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTokenExpired(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent settingsIntent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(settingsIntent);
        finish();
    }

    @Override
    public void onBackPressed(){
        Intent settingsIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(settingsIntent);
        finish();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("updateInterval")){
            SharedPreferences.Editor userEditor = sharedPref.edit();
            userEditor.putInt(key, sharedPreferences.getInt(key, 5));
            userEditor.commit();
        } else if(key.equals("radius")){
            SharedPreferences.Editor userEditor = sharedPref.edit();
            userEditor.putInt(key, sharedPreferences.getInt(key, 20));
            userEditor.commit();
        }
    }
}