package de.hhn.aib.swlab.wise1920.group05.exercise2.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import de.hhn.aib.swlab.wise1920.group05.exercise2.R;
import de.hhn.aib.swlab.wise1920.group05.exercise2.databinding.ActivityLoginBinding;
import de.hhn.aib.swlab.wise1920.group05.exercise2.model.User;
import de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel.LoginListener;
import de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel.LoginViewModel;

import static de.hhn.aib.swlab.wise1920.group05.exercise2.view.MainActivity.MY_PERMISSION_REQUEST_LOCATION;
import static de.hhn.aib.swlab.wise1920.group05.exercise2.view.MainActivity.MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE;
import static de.hhn.aib.swlab.wise1920.group05.exercise2.view.MainActivity.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity implements LoginListener {


    private SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        LoginViewModel loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        binding.setLoginViewModel(loginViewModel);
        checkSelfPermissionAccessFineLocation();
        checkSelfPermissionReadExternalStorage();
        checkSelfPermissionWriteExternalStorage();

        loginViewModel.setLoginListener(this);
    }

    private void checkSelfPermissionAccessFineLocation(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_LOCATION);
        }
    }

    private void checkSelfPermissionWriteExternalStorage(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(LoginActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void checkSelfPermissionReadExternalStorage(){
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(LoginActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }



    @Override
    public void onSuccess(String message, LiveData<User> userLiveData) {
        userLiveData.observe(this, user -> {
            userPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPref.edit();
            userEditor.putString("username", user.getUsername());
            userEditor.putString("id", user.getId());
            userEditor.putString("token", user.getToken());
            userEditor.putString("description", user.getDescription());
            userEditor.apply();
        });
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(loginIntent);
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
    public void onSignUp(){
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
