package com.donext.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.donext.app.R;
import com.donext.app.adapters.TaskAdapter;
import com.donext.app.database.DatabaseHelper;
import com.donext.app.database.SessionManager;
import com.donext.app.models.Task;

import java.util.List;

/**
 * TasksActivity
 * Main screen that displays user tasks and allows adding new tasks
 */

public class TasksActivity extends AppCompatActivity {

    private ListView lvTasks;
    private Button btnAddTodo;
    private LinearLayout navTasks, navProfile, navAbout;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Initialize helpers
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Redirect if not logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Bind UI
        lvTasks = findViewById(R.id.lvTasks);
        btnAddTodo = findViewById(R.id.btnAddTodo);
        navTasks = findViewById(R.id.navTasks);
        navProfile = findViewById(R.id.navProfile);
        navAbout = findViewById(R.id.navAbout);

        // Load tasks

        loadTasks();

        // Add task button
        btnAddTodo.setOnClickListener(v -> showAddTaskDialog());

        // Navigation
        navTasks.setOnClickListener(v -> { /* Already here */ });
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
        navAbout.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    /**
     * Loads tasks from database for current user
     */
    private void loadTasks() {
        String username = sessionManager.getUsername();
        taskList = dbHelper.getTasksForUser(username);
        taskAdapter = new TaskAdapter(this, taskList);
        lvTasks.setAdapter(taskAdapter);
        btnAddTodo.setVisibility(View.VISIBLE);
    }


    /**
     * Opens dialog to add a new task
     */
    private void showAddTaskDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText etTitle = dialogView.findViewById(R.id.etTaskTitle);
        TextView tvDate = dialogView.findViewById(R.id.tvDate);
        TextView tvTime = dialogView.findViewById(R.id.tvTime);
        View btnClose = dialogView.findViewById(R.id.btnCloseDialog);

        // Set current date and time
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String defaultDate = new java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(cal.getTime());
        String defaultTime = new java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(cal.getTime());
        
        if (tvDate != null) tvDate.setText(defaultDate);
        if (tvTime != null) tvTime.setText(defaultTime);
        // Date picker
        if(tvDate != null) {
            tvDate.setOnClickListener(v -> {
                java.util.Calendar c = java.util.Calendar.getInstance();
                new android.app.DatePickerDialog(this,
                        (view, year, month, dayOfMonth) -> {
                            String picked = new java.text.SimpleDateFormat("MMM d, yyyy",
                                    java.util.Locale.getDefault())
                                    .format(new java.util.GregorianCalendar(year, month, dayOfMonth).getTime());
                            tvDate.setText(picked);

                        },
                        c.get(java.util.Calendar.YEAR),
                        c.get(java.util.Calendar.MONTH),
                        c.get(java.util.Calendar.DAY_OF_MONTH)
                ).show();
            });
        }
        // Time picker
        if(tvTime != null) {
                   tvTime.setOnClickListener(v -> {
                       java.util.Calendar c = java.util.Calendar.getInstance();
                       new android.app.TimePickerDialog(this,
                               (view, hourOfDay, minute) -> {
                                   java.util.Calendar picked = java.util.Calendar.getInstance();
                                   picked.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
                                   picked.set(java.util.Calendar.MINUTE, minute);
                                   String t = new java.text.SimpleDateFormat("h:mm a",
                                           java.util.Locale.getDefault()).format(picked.getTime());
                                   tvTime.setText(t);

                           },
                               c.get(java.util.Calendar.HOUR_OF_DAY),
                               c.get(java.util.Calendar.MINUTE),
                               false
                       ).show();
                   });
               }


        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create();

                // Transparent background for rounded dialog
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                // Close button
                if (btnClose != null) {
                    btnClose.setOnClickListener(v -> dialog.dismiss());
                }

                // Save task
                dialogView.findViewById(R.id.btnSaveTask).setOnClickListener(v -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) {
                        Toast.makeText(this, getString(R.string.enter_task), Toast.LENGTH_SHORT).show();
                        return;
                    }

                String selectedDate = tvDate != null ? tvDate.getText().toString() : defaultDate;
                String selectedTime = tvTime != null ? tvTime.getText().toString() : defaultTime;

                Task newTask = new Task(0, title, false, sessionManager.getUsername(), selectedDate, selectedTime);
                    long id = dbHelper.addTask(newTask);
                    newTask.setId((int) id);
                    taskList.add(0, newTask);
                    taskAdapter.notifyDataSetChanged();
                    Toast.makeText(this, getString(R.string.task_added), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

                // Cancel button
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
                dialog.show();



            }



        }
