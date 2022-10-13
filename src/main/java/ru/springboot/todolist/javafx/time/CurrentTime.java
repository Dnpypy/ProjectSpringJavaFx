package ru.springboot.todolist.javafx.time;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CurrentTime {

    /**
     * показывает текущее время
     */
    public static String currentTime(){
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return time.format(formatter);
    }
}
