package com.donext.app.models;

public class Task {
    private int id;
    private String title;
    private boolean isCompleted;
    private String username; // owner

    private String date;
    private String time;

    public Task() {}

    public Task(int id, String title, boolean isCompleted, String username, String date, String time) {
        this.id = id;
        this.title = title;
        this.isCompleted = isCompleted;
        this.username = username;
        this.date = date;
        this.time = time;

    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}

