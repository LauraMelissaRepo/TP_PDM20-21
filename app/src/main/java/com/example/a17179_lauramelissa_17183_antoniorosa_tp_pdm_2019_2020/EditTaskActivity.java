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

    public static final String TASK_KEY = "task";
    private EditText taskDescriptionEditText;

    /***
     * In this method you access the fields in the xml, create the add save button listener and finally,
     * update the description in the database
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.editTaskToolbar);

        this.taskDescriptionEditText = findViewById(R.id.new_description_task);

        //receive the id task
        long taskId = getIntent().getLongExtra("taskId", -1);
        if(taskId == -1) {
            finish();
        }

        //receive the task description than we want to edit
        String t = getIntent().getStringExtra(TASK_KEY);
        this.taskDescriptionEditText.setText(t);

        //get the task that as the id that we passed
        Task oldTask = TaskDatabase.getInstance(getApplicationContext()).taskDao().get(taskId);

        //listener to update the description on the database
        ExtendedFloatingActionButton saveButton = findViewById(R.id.save_task_btn);
        saveButton.setOnClickListener(v -> {
            String newDescription = taskDescriptionEditText.getText().toString();

            oldTask.updateDescription(newDescription);
            TaskDatabase.getInstance(getApplicationContext()).taskDao().update(oldTask);

            finish();
        });
    }
}
