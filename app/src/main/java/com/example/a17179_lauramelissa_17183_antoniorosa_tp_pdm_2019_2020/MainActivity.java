package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.Task;
import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.database.TaskDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 *  This class and those derived from it, implement the requirements 2 and 5
 */
public class MainActivity extends AppCompatActivity {

    private TaskAdapter adapter;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //BottomNav
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_menu);
        bottomNav.setSelectedItemId(R.id.nav_tasks);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.tasks);

        // define the components for recyclerView
        RecyclerView tasksList = findViewById(R.id.tasks_list);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        tasksList.setLayoutManager(lm);
        this.adapter = new TaskAdapter();
        tasksList.setAdapter(adapter);

        //Button to create new task and his listener
        FloatingActionButton createButton = findViewById(R.id.create_task_btn);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Function responsible to get the item click from the Bottom Navigation Bar, use
     * a listener to create a new intent and move the user to a new Activity.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Intent resultIntent = null;
                switch (item.getItemId()) {
                    case R.id.nav_people:
                        resultIntent = new Intent(MainActivity.this, PeopleActivity.class);
                        break;
                    case R.id.nav_map:
                        resultIntent = new Intent(MainActivity.this, LocationActivity.class);
                        break;
                }
                if (resultIntent != null){
                    startActivity(resultIntent);
                    MainActivity.this.finish();
                    return true;
                }
                return false;
            };


    /**
     * Function called on the start of the activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        refreshData();
    }

    /**
     * Function used to refresh data from database into list's used on activity.
     */
    private void refreshData() {
        List<Task> tasks = TaskDatabase.getInstance(getApplicationContext())
            .taskDao()
            .getAll();
        adapter.setData(tasks);
    }


    public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {

        /**
         * Initialize the dataset of the Adapter.
         * Defines de task list
         * @param data List<Task> containing the data to populate views to be used
         *             by recyclerView.
         */
        private List<Task> data = new ArrayList<>();


        public void setData(List<Task> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        /**
         * Function used to create new views
         * the inflater layout will create a view from the task_list.xml
         */
        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list, parent, false);
            return new TaskViewHolder(view);
        }

        // Replace the contents of a view
        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = data.get(position);
            holder.bind(task);
        }

        // Return the size of data
        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox isDone;
        private final TextView task_description;
        private CardView cardView;
        private Task task;

        //use itemView to search for items
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.task_description = itemView.findViewById(R.id.task_item_description);
            this.isDone = itemView.findViewById(R.id.task_item_check_box);
            this.cardView = itemView.findViewById(R.id.card_item_view);

            builder = new AlertDialog.Builder(MainActivity.this);

            //listener to the check box
            this.isDone.setOnCheckedChangeListener((button, isChecked) -> {
                this.task.setDone(isChecked);
                TaskDatabase.getInstance(getApplicationContext()).taskDao().update(task);
            });

            //listener to the item view to edit the information.
            // Send the information (id and description) than the user wants to change to the edit activity
            this.cardView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
                intent.putExtra("taskId", this.task.getId());
                intent.putExtra(EditTaskActivity.TASK_KEY, this.task_description.getText().toString());
                startActivity(intent);
            });

            //listener to the item view to delete it
            this.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                   builder.setMessage(R.string.taskDeleteMessage);
                   builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           TaskDatabase.getInstance(getApplicationContext()).taskDao().delete(task);
                           refreshData();
                       }
                   });
                       builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.cancel();
                           }
                       });
                       AlertDialog alert = builder.create();
                       alert.setTitle(getString(R.string.questionToDelete));
                       alert.show();
                       return false;
                }
            });
        }

        //Puts the data on the items
        public void bind(Task task) {
            this.task = task;
            this.task_description.setText(task.getTask());
            this.isDone.setChecked(task.isDone());
        }
    }
}
