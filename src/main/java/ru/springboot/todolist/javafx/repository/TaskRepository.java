package ru.springboot.todolist.javafx.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.springboot.todolist.javafx.entity.Task;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {

    List<Task> findByTaskContainingIgnoreCase(String task);

}
