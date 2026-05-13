package com.donext.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;

import com.donext.app.R;
import com.donext.app.database.DatabaseHelper;
import com.donext.app.models.User;

public class SignupActivity extends AppCompatActivity {

    private EditText  etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvSignIn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DatabaseHelper(this);


        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);

        setupSignInLink();

        btnSignUp.setOnClickListener(v -> attemptSignup());
    }

    private void attemptSignup() {

        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if ( username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.passwords_not_match), Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.isUsernameTaken(username)) {
            Toast.makeText(this, getString(R.string.username_taken), Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(username, email, password);
        boolean success = dbHelper.registerUser(newUser);

        if (success) {
            Toast.makeText(this, getString(R.string.signup_success), Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSignInLink() {
        String fullText = getString(R.string.have_account);
        SpannableString spannable = new SpannableString(fullText);

        int signInStart = fullText.indexOf("Sign In");
        int signInEnd = signInStart + "Sign In".length();

        // "Already have an account? " → purple
        spannable.setSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(this, R.color.purple_primary)),
                0, signInStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // "Sign In" → black
        spannable.setSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(this, R.color.text_dark)),
                signInStart, signInEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // "Sign In" → clickable, no underline
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                finish();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, signInStart, signInEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSignIn.setText(spannable);
        tvSignIn.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
