package com.dev.simpsonsays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import tyrantgit.explosionfield.ExplosionField;

public class SplashScreen extends MainActivity  {

    private ExplosionField explosionField;
    private ImageView imageViewLogo;
    private TextView tvSimpson;
    private  MainActivity mainActivity;



    private void statusBar() {
        Window window = SplashScreen.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(SplashScreen.this, R.color.light_blue));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        findId();
        initiateHandler();
        statusBar();
        onClick();
        BackgroundMusic();
    }

    private void onClick() {
        imageViewLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explosionField.explode(view);
            }
        });

        tvSimpson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explosionField.explode(view);
            }
        });
    }

    private void findId() {
        imageViewLogo = findViewById(R.id.image_view_logo);
        tvSimpson = findViewById(R.id.tv_simpson);
        explosionField = ExplosionField.attach2Window(this);
    }

    private void initiateHandler() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageViewLogo.performClick();
                tvSimpson.performClick();
            }
        }, 1000);


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 2500);
    }

    private void BackgroundMusic() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.simpsons_homer);
        mediaPlayer.start();
    }


}