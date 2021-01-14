package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String task;
    private boolean isDone;

    public Task(long id, String task, boolean isDone) {
        this.id = id;
        this.task = task;
        this.isDone = isDone;
    }

    public long getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public boolean isDone() {
        return isDone;
    }
}
