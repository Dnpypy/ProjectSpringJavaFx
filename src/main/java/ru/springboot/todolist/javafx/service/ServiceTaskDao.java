package ru.springboot.todolist.javafx.service;

import javafx.collections.ObservableList;
import ru.springboot.todolist.javafx.entity.Task;

public interface ServiceTaskDao {

   // boolean addTask(Task task);

  //  boolean deleteTask(Task task);

   // boolean updateTask(Task task);

  boolean completeTask(Task task, String s);

    void add(Task task);

    void update(Task task);

    void delete(Task task);

    ObservableList<Task> findAll();

    ObservableList<Task> find(String text);


}
