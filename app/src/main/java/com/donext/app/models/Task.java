package com.donext.app.models;
/**
 * Task model class represents a single to-do task in the system.
 * It stores task details such as title, completion status, owner,
 * and scheduled date & time.
 */
public class Task {
    // Unique task ID (primary key in database)
    private int id;

    // Task title
    private String title;

    // Completion status (true = completed, false = pending)
    private boolean isCompleted;

    // Username of the task owner
    private String username;

    // Scheduled date of the task
    private String date;

    // Scheduled time of the task
    private String time;

    //Default constructor
    public Task() {}

    //Parameterized constructor to initialize all task fields
    public Task(int id, String title, boolean isCompleted, String username, String date, String time) {
        this.id = id;
        this.title = title;
        this.isCompleted = isCompleted;
        this.username = username;
        this.date = date;
        this.time = time;

    }

    // ================= GETTERS AND SETTERS =================//
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

