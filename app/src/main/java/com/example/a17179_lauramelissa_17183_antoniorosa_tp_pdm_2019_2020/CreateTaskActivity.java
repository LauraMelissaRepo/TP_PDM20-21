package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.EditText;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.Task;
import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.database.TaskDatabase;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class CreateTaskActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText taskDescription;

    /***
     * Neste método acede-se aos campos do xml, cria-se o listener do botão adicionar e por fim,
     * vai-se buscar a descrição inserida pelo utilizador e insere-se esta na base de dados
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Criar tarefa");

        this.taskDescription = findViewById(R.id.location_description);

        ExtendedFloatingActionButton btn = findViewById(R.id.save_task_btn);

        btn.setOnClickListener(v -> {
            String description = taskDescription.getText().toString();

            //criar uma nova tarefa com o id do auto-generate, a descrição e com a checkBox não preenchida
            Task task = new Task(0, description, false);
            TaskDatabase
                .getInstance(getApplicationContext())
                .taskDao()
                .insert(task);
            finish();
        });
    }
}
