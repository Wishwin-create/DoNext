package com.donext.app.models;

/**
 * User model class represents a registered user in the system.
 * It stores user account details such as username, email, and password.
 */

public class User {
    // Unique user ID (primary key in database)
    private int id;

    // Username used for login and identification
    private String username;

    // User's email address
    private String email;

    // User's password
    private String password;

    //Default constructor

    public User() {}

    //Constructor used during user registration

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    //Constructor used when retrieving user from database
    public User(int id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // ================= GETTERS AND SETTERS =================//
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
