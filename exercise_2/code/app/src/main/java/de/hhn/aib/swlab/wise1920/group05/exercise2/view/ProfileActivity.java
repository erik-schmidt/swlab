package de.hhn.aib.swlab.wise1920.group05.exercise2.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.hhn.aib.swlab.wise1920.group05.exercise2.R;
import de.hhn.aib.swlab.wise1920.group05.exercise2.databinding.ActivityProfileBinding;
import de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel.ProfileViewModel;
import de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel.WebserviceListener;

public class ProfileActivity extends AppCompatActivity implements WebserviceListener {

    private SharedPreferences sharedPref;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        binding.setProfileViewModel(profileViewModel);
        profileViewModel.setWebserviceListener(this);
        sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        TextView usernameTV = findViewById(R.id.profileUsernameEditText);
        EditText descriptionET = findViewById(R.id.profileDescriptionEditText);
        EditText passwordET = findViewById(R.id.profilePasswordEditText);
        usernameTV.setText(sharedPref.getString("username", null));
        descriptionET.setText(sharedPref.getString("description", null));
        passwordET.setText(sharedPref.getString("password", null));
        saveUserSettings();
    }

    @Override
    public void onSuccess(String message) {
        try {
            checkPassword();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor sharedPreferencesEditor = sharedPref.edit();
            sharedPreferencesEditor.putString("description", profileViewModel.getDescription());
            sharedPreferencesEditor.putString("password", profileViewModel.getPassword());
            sharedPreferencesEditor.apply();
            Intent saveProfileIntent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(saveProfileIntent);
        } catch (IllegalArgumentException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void checkPassword(){
        if(profileViewModel.getPassword() == null || profileViewModel.getPassword().trim().isEmpty()){
            throw new IllegalArgumentException("Password can not be empty");
        }
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
        Intent profileIntent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(profileIntent);
        finish();
    }


    private void saveUserSettings(){
        profileViewModel.setPassword(sharedPref.getString("password", null));
        profileViewModel.setToken(sharedPref.getString("token", null));
        profileViewModel.setUserID(sharedPref.getString("id", null));
        profileViewModel.setUsername(sharedPref.getString("username", null));
        profileViewModel.setDescription(sharedPref.getString("description", null));
    }

    @Override
    public void onBackPressed(){
        Intent profileIntent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(profileIntent);
        finish();
    }
}
