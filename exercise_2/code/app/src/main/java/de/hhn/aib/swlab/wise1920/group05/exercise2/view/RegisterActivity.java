package de.hhn.aib.swlab.wise1920.group05.exercise2.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import de.hhn.aib.swlab.wise1920.group05.exercise2.R;
import de.hhn.aib.swlab.wise1920.group05.exercise2.databinding.ActivityRegisterBinding;
import de.hhn.aib.swlab.wise1920.group05.exercise2.model.User;
import de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel.RegisterListener;
import de.hhn.aib.swlab.wise1920.group05.exercise2.viewModel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity implements RegisterListener {

    private SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        RegisterViewModel registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        binding.setRegisterViewModel(registerViewModel);
        registerViewModel.setRegisterListener(this);
    }

    @Override
    public void onSuccess(String message, LiveData<User> userLiveData) {
        userLiveData.observe(this, user -> {
            userPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPref.edit();
            userEditor.putString("username", user.getUsername());
            userEditor.putString("id", user.getId());
            userEditor.putString("token", user.getToken());
            userEditor.putString("description", user.getDescription());
            userEditor.apply();
        });
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(registerIntent);
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
    public void onCancel() {
        Intent cancelIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(cancelIntent);
    }
}
