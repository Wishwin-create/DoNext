package com.donext.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.donext.app.R;

/**
 * AboutActivity
 * Displays app/developer information screen.
 */

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Link this activity to its XML layout
        setContentView(R.layout.activity_about);

        // Find Exit button from layout
        Button btnExit = findViewById(R.id.btnExit);

        // Close the activity when Exit button is clicked
        btnExit.setOnClickListener(v -> finish());
    }
}
