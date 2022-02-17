package de.hhn.aib.swlab.wise1920.group05.exercise2.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import de.hhn.aib.swlab.wise1920.group05.exercise2.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        ListPreference alarmTonePreference = findPreference("visibility");
        if(alarmTonePreference != null){
            alarmTonePreference.setVisible(true);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("visibility")){
            Preference pref = findPreference(key);
            if (pref != null) {
                pref.setDefaultValue(sharedPreferences.getString(key, ""));
            }
        }
    }
}

