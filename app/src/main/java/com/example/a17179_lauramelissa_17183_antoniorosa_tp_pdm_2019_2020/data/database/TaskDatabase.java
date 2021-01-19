package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.Task;

@Database(entities = {Task.class}, version = 2, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    private static TaskDatabase instance;

    public static TaskDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room
                .databaseBuilder(context, TaskDatabase.class, "task_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        }

       return instance;
    }

    public abstract TaskDao taskDao();
}

