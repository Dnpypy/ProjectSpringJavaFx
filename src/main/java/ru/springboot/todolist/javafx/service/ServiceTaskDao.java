package ru.springboot.todolist.javafx.service;

import javafx.collections.ObservableList;
import org.springframework.data.domain.Page;
import ru.springboot.todolist.javafx.entity.Task;

public interface ServiceTaskDao {

    boolean completeTask(Task task, String s);

    void add(Task task);

    void update(Task task);

    void delete(Task task);

    ObservableList<Task> findAll();

    Page findAll(int from, int count);

    Page findAll(int from, int count, String... text);

    ObservableList<Task> find(String text);


}
