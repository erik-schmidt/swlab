package de.hhn.aib.swlab.wise1920.group05.exercise1;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        ListPreference alarmTonePreference = findPreference("alarmtones");
        if(alarmTonePreference != null){
            alarmTonePreference.setVisible(true);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("alarmtones")){
            Preference pref = findPreference(key);
            pref.setDefaultValue(sharedPreferences.getString(key, ""));
        }
    }
}
