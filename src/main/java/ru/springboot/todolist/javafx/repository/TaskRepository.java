package ru.springboot.todolist.javafx.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.springboot.todolist.javafx.entity.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByTaskContainingIgnoreCase(String task);

    Page<Task> findByTaskContainingIgnoreCase(String fio, Pageable pageable);

}
