package com.donext.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.donext.app.R;
import com.donext.app.database.DatabaseHelper;
import com.donext.app.database.SessionManager;
import com.google.android.material.imageview.ShapeableImageView; // ← changed


/**
 * ProfileActivity
 * Handles user profile display, edit, image update, and sign out
 */

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private Button btnEdit, btnSignOut;
    private ImageButton btnBack;
    private ShapeableImageView ivProfileImage;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize helpers
        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);

        // Bind UI elements
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);
        btnSignOut = findViewById(R.id.btnSignOut);
        ivProfileImage = findViewById(R.id.ivProfileImage);

        // Load session data into UI
        tvUserName.setText(sessionManager.getUsername());
        tvUserEmail.setText(sessionManager.getEmail());

        loadProfileImage();

        // Image picker launcher (select profile image)
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Persist permission for image access
                        getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        // Save image URI in session
                        sessionManager.saveProfileImage(uri.toString());
                        // Display image in profile
                        ivProfileImage.setImageURI(uri);         // ShapeableImageView clips to circle
                    }
                });

        // Open image picker on click
        ivProfileImage.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*"));

        // Navigation & actions
        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> showEditDialog());
        btnSignOut.setOnClickListener(v -> showSignOutDialog());
    }

    /**
     * Loads saved profile image if exists
     */
    private void loadProfileImage() {
        String imagePath = sessionManager.getProfileImage();
        if (imagePath != null) {
            try {
                ivProfileImage.setImageURI(Uri.parse(imagePath));
            } catch (Exception e) {
                ivProfileImage.setImageResource(R.drawable.ic_default_avatar);
            }
        }
    }

    /**
     * Shows edit profile dialog
     */
    private void showEditDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_edit_profile, null);

        EditText etUsername = dialogView.findViewById(R.id.etUsername);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        ImageButton btnClose = dialogView.findViewById(R.id.btnCloseDialog);

        etUsername.setText(sessionManager.getUsername());
        etEmail.setText(sessionManager.getEmail());
        etUsername.setSelection(etUsername.getText().length());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Close dialog actions
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnCancelEdit).setOnClickListener(v -> dialog.dismiss());

        // Save profile changes
        dialogView.findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();

            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newEmail.isEmpty()) {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String oldUsername = sessionManager.getUsername();
            int userId = sessionManager.getUserId();
            boolean updated = dbHelper.updateUserProfile(userId, newUsername, newEmail);

            if (updated) {
                // Update tasks linked to old username
                dbHelper.updateTasksUsername(oldUsername, newUsername);
                // Update session
                sessionManager.updateSession(newUsername, newEmail);
                // Update UI
                tvUserName.setText(newUsername);
                tvUserEmail.setText(newEmail);
                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Update failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    /**
     * Shows sign out confirmation dialog
     */

    private void showSignOutDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_sign_out, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogView.findViewById(R.id.btnCloseDialog).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnCancelSignOut).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnConfirmSignOut).setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

}