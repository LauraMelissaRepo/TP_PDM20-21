package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.Task;
import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.database.TaskDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TaskAdapter adapter;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_menu);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tarefas");

        RecyclerView tasksList = findViewById(R.id.tasks_list);
        FloatingActionButton createButton = findViewById(R.id.create_task_btn);

        //definir os componentes para a recyclerView
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        tasksList.setLayoutManager(lm);

        this.adapter = new TaskAdapter();
        tasksList.setAdapter(adapter);

        this.checkBox = findViewById(R.id.task_item_check_box);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
                startActivity(intent);
            }
        });

//        checkBox.setOnClickListener(v -> {
//            if(checkBox.isChecked()) {
//                TaskDatabase.getInstance(getApplicationContext())
//                    .taskDao()
//                    .update();
//            }
//        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent resultIntent = null;

                switch (item.getItemId()) {
                    case R.id.nav_people:
                        resultIntent = new Intent(MainActivity.this, PeopleActivity.class);
                        break;
                    case R.id.nav_map:
                        resultIntent = new Intent(MainActivity.this, PeopleActivity.class);
                        break;
                }
                startActivity(resultIntent);
                return true;
            }
        };

    /**
     * Buscar a lista das tarefas à base de dados cada vez que se inicia a aplicação
     */
    @Override
    protected void onStart() {
        super.onStart();
        List<Task> tasks = TaskDatabase.getInstance(getApplicationContext())
            .taskDao()
            .getAll();
        adapter.setData(tasks);
    }

    public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {

        //definir a lista de tasks
        private List<Task> data = new ArrayList<>();

        public void setData(List<Task> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //o layout inflater vai criar uma view a partir do task_list.xml (xml descreve uma view)
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = data.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        private final TextView task_description;
        private final CheckBox isDone;

        //utilizar o itemView para procurar os items
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            task_description = itemView.findViewById(R.id.task_item_description);
            isDone = itemView.findViewById(R.id.task_item_check_box);
        }

        public void bind(Task task) {
            task_description.setText(task.getTask());
            isDone.setChecked(task.isDone());
        }
    }


}
