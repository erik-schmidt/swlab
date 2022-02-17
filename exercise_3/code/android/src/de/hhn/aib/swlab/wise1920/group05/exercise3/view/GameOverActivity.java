package de.hhn.aib.swlab.wise1920.group05.exercise3.view;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;

import de.hhn.aib.swlab.wise1920.group05.exercise3.R;

public class GameOverActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        mediaPlayer = MediaPlayer.create(GameOverActivity.this, R.raw.gameover);
        mediaPlayer.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Intent homeIntent = new Intent(GameOverActivity.this, MainActivity.class);
        startActivity(homeIntent);
        finish();
        return super.onTouchEvent(event);
    }
}
