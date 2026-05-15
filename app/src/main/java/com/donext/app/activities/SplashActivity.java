package com.donext.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.donext.app.R;
import com.donext.app.database.SessionManager;


/**
 * SplashActivity
 * Shows app splash screen and routes user based on login session
 */

public class SplashActivity extends AppCompatActivity {

    // Duration of splash screen display (in milliseconds)
    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle Android 12+ system splash screen behavior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen().setOnExitAnimationListener(
                    splashScreenView -> splashScreenView.remove()
            );
        }

        // Set splash layout
        setContentView(R.layout.activity_splash);

        // Delay navigation to next screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check user session
            SessionManager session = new SessionManager(SplashActivity.this);
            Intent intent;
            // Navigate based on login status
            if (session.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, TasksActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }

}