package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.EditText;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.Task;
import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.database.TaskDatabase;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText taskDescription;

    /***
     * In this method you access the fields in the xml, create the add button listener and finally,
     * search for the description entered by the user and insert it in the database
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.createTaskToolBarTitle);

        this.taskDescription = findViewById(R.id.task_description);

        ExtendedFloatingActionButton btn = findViewById(R.id.save_task_btn);
        //listener to create the task on the database
        btn.setOnClickListener(v -> {
            String description = taskDescription.getText().toString();

            // create a new task on database with the auto-generate id, description and with the checkbox not filled
            Task task = new Task(0, description, false);
            TaskDatabase
                .getInstance(getApplicationContext())
                .taskDao()
                .insert(task);
            finish();
        });
    }
}
