package ru.springboot.todolist.javafx.fxml;

import org.springframework.stereotype.Component;

/**
 * в этом классе прописываем путь до fxml,
 * который потом передаем в родительский класс
 */
@Component
public class MainView extends SpringFxmlView {

    private static final String FXML_MAIN = "todolist.fxml";

    public MainView() {
        super(MainView.class.getClassLoader().getResource(FXML_MAIN));
    }
}
