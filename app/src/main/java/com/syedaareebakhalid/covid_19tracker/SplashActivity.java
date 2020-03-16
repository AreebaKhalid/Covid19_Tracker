package com.syedaareebakhalid.covid_19tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        progressBar = (ProgressBar) findViewById(R.id.progress_loader);

        Runnable runSplash = new Runnable() {
            @Override
            public void run() {
                handleIntent();
            }
        };
        Handler hndSplash = new Handler();
        hndSplash.postDelayed(runSplash, 2000);
    }

    public void handleIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
