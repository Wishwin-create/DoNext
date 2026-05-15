package com.donext.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.donext.app.models.Task;
import com.donext.app.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper manages all SQLite database operations
 * including user management and task management.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database name and version
    private static final String DATABASE_NAME = "donext.db";
    private static final int DATABASE_VERSION = 2;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    // Tasks table
    private static final String TABLE_TASKS = "tasks";
    private static final String COL_TASK_ID = "id";
    private static final String COL_TASK_TITLE = "title";
    private static final String COL_TASK_COMPLETED = "is_completed";
    private static final String COL_TASK_USERNAME = "username";
    private static final String COL_TASK_DATE = "date";
    private static final String COL_TASK_TIME = "time";

   //Constructor - initializes database helper
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
     // Creates database tables when app is first installed
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_EMAIL + " TEXT, " +
                COL_PASSWORD + " TEXT)");

        // Create Tasks table
        db.execSQL("CREATE TABLE " + TABLE_TASKS + " (" +
                COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TASK_TITLE + " TEXT, " +
                COL_TASK_COMPLETED + " INTEGER DEFAULT 0, " +
                COL_TASK_USERNAME + " TEXT, " +
                COL_TASK_DATE + " TEXT, " +
                COL_TASK_TIME + " TEXT)");
    }


    //Handles database upgrades (drops and recreates tables)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }


    // =================== USER OPERATIONS ===================//

    // Registers a new user in the database

    public boolean registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, user.getUsername());
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_PASSWORD, user.getPassword());
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }


    //Validates user login credentials
    public User loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD))
            );
            cursor.close();
            db.close();
            return user;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    //Checks if username already exists
    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USERNAME + "=?", new String[]{username},
                null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    //Retrieves user details using username
    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USERNAME + "=?", new String[]{username},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD))
            );
            cursor.close();
            db.close();
            return user;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    // =================== TASK OPERATIONS ===================//

    // Adds a new task to the database

    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_TITLE, task.getTitle());
        values.put(COL_TASK_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COL_TASK_USERNAME, task.getUsername());
        values.put(COL_TASK_DATE, task.getDate());
        values.put(COL_TASK_TIME, task.getTime());
        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    // Retrieves tasks for a specific user
    public List<Task> getTasksForUser(String username) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null,
                COL_TASK_USERNAME + "=?", new String[]{username},
                null, null, COL_TASK_ID + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_TITLE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_COMPLETED)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_TIME))
                );
                tasks.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return tasks;
    }

    //Updates a task in the database(title, status, date, time)
    public boolean updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_TITLE, task.getTitle());
        values.put(COL_TASK_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COL_TASK_DATE, task.getDate());
        values.put(COL_TASK_TIME, task.getTime());
        int rows = db.update(TABLE_TASKS, values,
                COL_TASK_ID + "=?", new String[]{String.valueOf(task.getId())});
        db.close();
        return rows > 0;
    }

    // Deletes a task from database
    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TASKS,
                COL_TASK_ID + "=?", new String[]{String.valueOf(taskId)});
        db.close();
        return rows > 0;
    }

    //Updates user profile (username + email)
    public boolean updateUserProfile(int userId, String newUsername, String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, newUsername);
        values.put(COL_EMAIL, newEmail);
        int rows = db.update(TABLE_USERS, values,
                COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    // updates tasks to use the new username after profile edit
    public void updateTasksUsername(String oldUsername, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_USERNAME, newUsername);
        db.update(TABLE_TASKS, values,
                COL_TASK_USERNAME + "=?", new String[]{oldUsername});
        db.close();
    }
}