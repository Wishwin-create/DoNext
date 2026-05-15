package com.donext.app.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.donext.app.R;
import com.donext.app.database.DatabaseHelper;
import com.donext.app.models.Task;

import java.util.List;


/**
 * TaskAdapter
 * Custom adapter for displaying tasks in ListView
 * Handles: view binding, edit, delete, and completion toggle
 */
public class TaskAdapter extends ArrayAdapter<Task> {

    private final Context context;
    private final List<Task> tasks;
    private final DatabaseHelper dbHelper;

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, R.layout.item_task, tasks);
        this.context = context;
        this.tasks = tasks;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Reuse view if available
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        }

        Task task = tasks.get(position);

        // Bind UI elements
        CheckBox cbTask = convertView.findViewById(R.id.cbTask);
        TextView tvTaskTitle = convertView.findViewById(R.id.tvTaskTitle);
        TextView tvTaskDateTime = convertView.findViewById(R.id.tvTaskDateTime);
        ImageButton btnEdit = convertView.findViewById(R.id.btnEdit);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

        // Set task title
        tvTaskTitle.setText(task.getTitle());
        // Show date and time below title if available
        if (task.getDate() != null && task.getTime() != null) {
            tvTaskDateTime.setText(task.getDate() + "  •  " + task.getTime());
            tvTaskDateTime.setVisibility(View.VISIBLE);
        } else {
            tvTaskDateTime.setVisibility(View.GONE);
        }

        // Prevent checkbox recycling issues
        cbTask.setOnCheckedChangeListener(null);
        cbTask.setChecked(task.isCompleted());

        // Apply strike-through if completed
        applyStrikeThrough(tvTaskTitle, task.isCompleted());

        // Toggle task completion
        cbTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            dbHelper.updateTask(task);
            applyStrikeThrough(tvTaskTitle, isChecked);
        });
        // Edit task
        btnEdit.setOnClickListener(v -> showEditDialog(position, task));

        // Delete task
        btnDelete.setOnClickListener(v -> showDeleteDialog(task));

        return convertView;
    }

    /**
     * Opens edit dialog for updating task
     */
     private void showEditDialog(int position, Task task) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null);
        EditText etTitle = dialogView.findViewById(R.id.etTaskTitle);
        TextView tvDate = dialogView.findViewById(R.id.tvDate);
        TextView tvTime = dialogView.findViewById(R.id.tvTime);
        ImageButton btnClose = dialogView.findViewById(R.id.btnCloseDialog);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);

         // Set dialog title
        tvDialogTitle.setText("Edit Your Task");

         // Pre-fill task data
        etTitle.setText(task.getTitle());
        etTitle.setSelection(task.getTitle().length());




        // Pre-fill with task's saved date/time
        tvDate.setText(task.getDate() != null ? task.getDate() : "");
        tvTime.setText(task.getTime() != null ? task.getTime() : "");


        // Date picker
        tvDate.setOnClickListener(v -> {
            java.util.Calendar c = java.util.Calendar.getInstance();
            new android.app.DatePickerDialog(context,
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

        // Time picker
        tvTime.setOnClickListener(v -> {
            java.util.Calendar c = java.util.Calendar.getInstance();
            new android.app.TimePickerDialog(context,
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

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        // Make rounded dialog background visible
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

         // Close button
        btnClose.setOnClickListener(v -> dialog.dismiss());

         // Save updates
        dialogView.findViewById(R.id.btnSaveTask).setOnClickListener(v -> {
            String newTitle = etTitle.getText().toString().trim();
            if (newTitle.isEmpty()) {
                Toast.makeText(context, context.getString(R.string.enter_task), Toast.LENGTH_SHORT).show();
                return;
            }
            task.setTitle(newTitle);
            task.setDate(tvDate.getText().toString());
            task.setTime(tvTime.getText().toString());
            dbHelper.updateTask(task);
            tasks.set(position, task);
            notifyDataSetChanged();
            Toast.makeText(context, context.getString(R.string.task_updated), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });


        // Cancel edit
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();


    }

    /**
     * Shows delete confirmation dialog
     */

        private void showDeleteDialog(Task task) {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_task, null);

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            dialogView.findViewById(R.id.btnCloseDialog).setOnClickListener(v -> dialog.dismiss());

            dialogView.findViewById(R.id.btnCancelDelete).setOnClickListener(v -> dialog.dismiss());

            dialogView.findViewById(R.id.btnConfirmDelete).setOnClickListener(v -> {
                dbHelper.deleteTask(task.getId());
                tasks.remove(task);
                notifyDataSetChanged();
                Toast.makeText(context, context.getString(R.string.task_deleted), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            dialog.show();
        }

    /**
     * Adds/removes strike-through effect
     */
        private void applyStrikeThrough(TextView tv, boolean completed) {
            if (completed) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
    }


}
