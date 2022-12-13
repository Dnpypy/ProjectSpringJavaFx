/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.springboot.todolist.javafx;

import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.springboot.todolist.javafx.fxml.MainView;
import ru.springboot.todolist.javafx.service.impls.ServiceTaskDaoImpl;
import ru.springboot.todolist.javafx.utils.ManagerLocale;

import java.util.Locale;


@SpringBootApplication
@Slf4j
public class Main extends JavaFxSpringIntegrator {

    @Autowired
    private MainView mainView;

    @Autowired
    ServiceTaskDaoImpl serviceTaskDao;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {

            super.start(primaryStage);
      //  System.out.println("Main class -> start ");
        log.info("Main class -> start ");
            loadMainFXML(ManagerLocale.RU_LOCALE, primaryStage);
        //System.out.println("loadMainFXML(ManagerLocale.RU_LOCALE, primaryStage);");
        log.info("loadMainFXML(ManagerLocale.RU_LOCALE, primaryStage);");
    }

    @Override
    public void init() throws Exception {

            super.init();

    }

    /**
     * загружает дерево компонентов и возвращает в виде VBox (корневой элемент в FXML)
     * getView загружает fxml, происходит внедрение самого FXML и возвращается корневой элемент,
     * который используется для уровня
     * уровню задаем ему разные характеристики .setMinHeight(650); .setMinWidth(750); .centerOnScreen();
     *
     * @param locale       текущая локаль
     * @param primaryStage главный уровень
     *                     scene сцена меняется(окна)
     */
    private void loadMainFXML(Locale locale, Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            // Scene scene = new Scene(currentRoot, 1000, 750);
            //нужные размеры окна при загрузке
            Scene scene = new Scene(mainView.getView(locale), 1000, 750);
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(650);
            primaryStage.setMinWidth(750);
            primaryStage.centerOnScreen();
            primaryStage.setTitle(mainView.getResourceBundle().getString("todo_list"));
           // System.out.println("loadMainFXML");
            log.info("loadMainFXML");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * старт приложения
     *
     * @param args аргументы в консоли
     */
    public static void main(String[] args) {
        launchSpringJavaFXApp(Main.class, args);
      //  System.out.println("main    launchSpringJavaFXApp(Main.class, args);");
        log.info("main    launchSpringJavaFXApp(Main.class, args);");
    }

    /**
     * останавливает приложение
     *
     * @throws Exception исключение
     */
    @Override
    public void stop() throws Exception {
        System.exit(0);
    }

}
