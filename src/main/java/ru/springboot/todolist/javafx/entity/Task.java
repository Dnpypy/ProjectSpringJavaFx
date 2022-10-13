package ru.springboot.todolist.javafx.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * В этом классе я добавляю JPA аннотации для сущности @Entity, @Id, @Column
 */
@Entity
@Table(name = "todo")
@EqualsAndHashCode(of = "id")

public class Task {

    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty task = new SimpleStringProperty("");

    private static Date date = new Date();
    private SimpleStringProperty time = new SimpleStringProperty(date.toString());

    private static String statusText = "не выполнена";
    private SimpleStringProperty status = new SimpleStringProperty(statusText);


    public Task() {
        this.task = task;
        this.time = time;
        this.status = status;

    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }


    @Column(name = "task", nullable = false, length = -1)
    public String getTask() {
        return task.get();
    }

    public SimpleStringProperty taskProperty() {
        return task;
    }

    public void setTask(String task) {
        this.task.set(task);
    }


    @Column(name = "task_create_time", nullable = false, length = -1)
    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }


    @Column(name = "status", nullable = true, length = -1)
    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", task=" + task +
                ", time=" + time +
                ", status=" + status +
                '}';
    }

}
