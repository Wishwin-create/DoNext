package com.donext.app.database;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager handles user login session using SharedPreferences.
 * It stores user authentication state and basic profile data locally.
 */
public class SessionManager {

    private static final String PREF_NAME = "DoNextSession";
    // Keys for storing session data

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    // Constructor initializes SharedPreferences
    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    //Creates a login session after successful authentication
    public void createLoginSession(int userId, String username, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }
    //Checks whether user is logged in or not
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    //Returns logged-in user ID
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    //Returns logged-in username
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    //Returns logged-in email
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    //Logs out the user and clears session data
    public void logout() {
        // Only remove session keys — profile images stored per user ID are preserved
        editor.remove(KEY_IS_LOGGED_IN);  // ← changed
        editor.remove(KEY_USER_ID);        // ← changed
        editor.remove(KEY_USERNAME);       // ← changed
        editor.remove(KEY_EMAIL);          // ← changed
        editor.apply();
    }
    // Updates username and email in session after profile edit
    public void updateSession(String username, String email) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    // Saves image under "profileImage_1", "profileImage_2" etc. per user
    public void saveProfileImage(String imagePath) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("profileImage_" + getUserId(), imagePath);
        editor.apply();
    }

    // Gets image for currently logged in user only
    public String getProfileImage() {
        return prefs.getString("profileImage_" + getUserId(), null);
    }
}