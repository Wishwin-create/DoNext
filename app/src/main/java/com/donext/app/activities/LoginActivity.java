package com.donext.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.donext.app.R;
import com.donext.app.database.DatabaseHelper;
import com.donext.app.database.SessionManager;
import com.donext.app.models.User;
import android.text.TextPaint;

/**
 * LoginActivity
 * Handles user login and navigation to signup screen
 */

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize database and session manager
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Bind UI elements
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        // Setup clickable sign-up text
        setupSignUpLink();

        // Login button click event
        btnLogin.setOnClickListener(v -> attemptLogin());
    }


    /**
     * Validates input and attempts login
     */
    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate empty fields
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        // Check credentials in database
        User user = dbHelper.loginUser(username, password);
        if (user != null) {
            sessionManager.createLoginSession(user.getId(), user.getUsername(), user.getEmail());
            Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, TasksActivity.class));
            finish();
        } else {
            Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Makes "Sign Up" text clickable and styled
     */
    private void setupSignUpLink() {
        String fullText = getString(R.string.no_account);
        SpannableString spannable = new SpannableString(fullText);

        int signUpStart = fullText.indexOf("Sign Up");
        int signUpEnd = signUpStart + "Sign Up".length();

        // "Don't have an account? " → purple
        spannable.setSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(this, R.color.purple_primary)),
                0, signUpStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // "Sign Up" → black
        spannable.setSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(this, R.color.text_dark)),
                signUpStart, signUpEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // "Sign Up" → clickable
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // remove underline
            }
        }, signUpStart, signUpEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSignUp.setText(spannable);
        tvSignUp.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
