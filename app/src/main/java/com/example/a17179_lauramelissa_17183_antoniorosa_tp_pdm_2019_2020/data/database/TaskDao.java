package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.database;

import android.widget.CheckBox;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.Task;

import java.util.List;

@Dao
public interface TaskDao {

    //Query para inserir uma tarefa
    @Insert
    long insert(Task task);

    //Atualizar uma tarefa
    @Update
    void update(Task updatedTask);

    //Query para ir buscar a lista de tarefa
    @Query("select * from task")
    List<Task> getAll();

    //Query para ir buscar aquela tarefa especifica com aquele id
    @Query("select * from task where id = :id")
    Task get(long id);

    //Eliminar uma tarefa
    @Delete
    void delete(Task Task);

}
