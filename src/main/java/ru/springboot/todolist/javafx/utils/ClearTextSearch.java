package ru.springboot.todolist.javafx.utils;

import javafx.scene.control.TextField;

public class ClearTextSearch {

    /**
     * Получает ссылку на объект текстового поля
     * Очищает текстовое поле пустым значением
     */
    public static void clearText(TextField text) {
        for (String line : text.getText().split("\n")) {
            if (line.contains(text.getText())) {
                text.setText(text.getText().replace(line, ""));
            }
        }
    }
}
