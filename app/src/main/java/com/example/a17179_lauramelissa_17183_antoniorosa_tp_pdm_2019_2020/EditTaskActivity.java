package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;


import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.Task;
import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.database.TaskDatabase;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class EditTaskActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Task task;
    public static final String TASK_KEY = "task";
    private EditText taskDescriptionEditText;
    private ExtendedFloatingActionButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        this.task = task;
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.editTaskToolbar);

        this.taskDescriptionEditText = findViewById(R.id.new_description_task);
        this.saveButton = findViewById(R.id.save_task_btn);

        long taskId = getIntent().getLongExtra("taskId", -1);

        if(taskId == -1) {
            finish();
        }

        String t = getIntent().getStringExtra(TASK_KEY);
        this.taskDescriptionEditText.setText(t);

        Task oldTask = TaskDatabase.getInstance(getApplicationContext()).taskDao().get(taskId);

        saveButton.setOnClickListener(v -> {
            String description = taskDescriptionEditText.getText().toString();

            oldTask.updateDescription(description);
            TaskDatabase.getInstance(getApplicationContext()).taskDao().update(oldTask);

            finish();
        });
    }
}
