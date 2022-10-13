package ru.springboot.todolist.javafx.fxml;

import org.springframework.stereotype.Component;


/**
 * в этом классе прописываем путь до fxml,
 * который потом передаем в родительский класс
 */
@Component
public class EditView extends SpringFxmlView {

        private static String FXML_EDIT = "todolistEdit.fxml";

        public EditView() {

                super(MainView.class.getClassLoader().getResource(FXML_EDIT));
        }


}
