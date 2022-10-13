package ru.springboot.todolist.javafx.controllers;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.springboot.todolist.javafx.entity.Task;
import ru.springboot.todolist.javafx.fxml.EditView;
import ru.springboot.todolist.javafx.fxml.MainView;
import ru.springboot.todolist.javafx.objects.Lang;
import ru.springboot.todolist.javafx.service.ServiceTaskDao;
import ru.springboot.todolist.javafx.time.CurrentTime;
import ru.springboot.todolist.javafx.utils.ClearTextSearch;
import ru.springboot.todolist.javafx.utils.DialogManager;
import ru.springboot.todolist.javafx.utils.ManagerLocale;

import java.io.IOException;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 * класс уже является spring bean, потомучто прописан аннотация @Component
 * печать всех spring бинов в консоль   printBeans()
 */
@SuppressWarnings("SpringJavaStaticMembersAutowiringInspection")
@Component
public class MainController extends Observable {

    /**
     * TaskDbDaoImpl todoListImpl = new TaskDbDaoImpl(); -> реализация БД отключена
     * Hibernate реализация, название переменной такое же, как у реализации базы данных
     */
    //private static TaskHibernateImpl todoListImpl = new TaskHibernateImpl();
    @Autowired
    private ServiceTaskDao todoListImpl;

    ObservableList<Task> taskObservableList;
    @Autowired
    private MainView mainView;

    @Autowired
    private EditView editView;

    @Autowired
    private EditDialogController editDialogController;

    @FXML
    public Button btnNew;
    @FXML
    public Button btnDelete;
    @FXML
    public Button btnSave;
    @FXML
    public Button btnUpdate;
    @FXML
    public Button btnComplete;
    @FXML
    public Button btnExit;
    @FXML
    public Button btnClear;

    @FXML
    public TextField txtSearch;
    @FXML
    public Button btnSearch;
    @FXML
    public TableView<Task> txtTodoList;

    @FXML
    public TableColumn<Task, String> mainTask;

    @FXML
    public TableColumn<Task, String> timeTask;

    @FXML
    public TableColumn<Task, Boolean> statusTask;

    @FXML
    public Label labelCount;

    @FXML
    public Label labelTime;

    @FXML
    private ComboBox changeLocaleBox;

    private Parent fxmlEdit;
    private final FXMLLoader fxmlLoader = new FXMLLoader();

    private Stage editDialogStage;

    private Stage mainStage;

    private ResourceBundle resourceBundle;

    private static final String RU_CODE = "ru";
    private static final String EN_CODE = "en";

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
        //this.resourceBundle = resources;
        this.resourceBundle = mainView.getResourceBundle();
        mainTask.setCellValueFactory(new PropertyValueFactory<Task, String>("task"));
        timeTask.setCellValueFactory(new PropertyValueFactory<Task, String>("time"));
        statusTask.setCellValueFactory(new PropertyValueFactory<Task, Boolean>("status"));
        setupClearButtonField(txtSearch);
       // System.out.println("initialize(");
        filldata();
        initListeners();
    }

    /**
     *  по-умолчанию показывать выбранный русский язык (можно текущие настройки языка сохранять в файл)
     */
    private void fillLangComboBox() {
        Lang langRU = new Lang(0, RU_CODE, resourceBundle.getString("ru"), ManagerLocale.RU_LOCALE);
        Lang langEN = new Lang(1, EN_CODE, resourceBundle.getString("en"), ManagerLocale.EN_LOCALE);
        System.out.println("fillLangComboBox()");
        changeLocaleBox.getItems().add(langRU);
        changeLocaleBox.getItems().add(langEN);

        if (ManagerLocale.getCurrentLang() == null) {
            ManagerLocale.setCurrentLang(langRU);
            changeLocaleBox.getSelectionModel().select(0);
        } else {
            changeLocaleBox.getSelectionModel().select(ManagerLocale.getCurrentLang().getIndex());
        }
    }

    private void filldata() throws IOException, ClassNotFoundException {
        fillTable();
        fillLangComboBox();
        updateCountLabel(); // вначале заполняется
        timeCurrent();      // вначале заполняется
    }

    /**
     * todoListImpl.findAll(); заполнение листа из таблицы
     * setItems(list); -> заполнение tableview
     * txtTodoList.refresh(); -> обновление tableView
     */
    private void fillTable() {
        //System.out.println("=========================");
        taskObservableList = todoListImpl.findAll();
        System.out.println("taskObservableList.isEm   pty = " + taskObservableList.isEmpty());
        txtTodoList.setItems(taskObservableList);
       // txtTodoList.refresh(); урал пока
    }

    private void setupClearButtonField(TextField txtSearch) {
        txtSearch.clear();
    }

    /**
     * слушатели событий
     * taskObservableList.addListener(new ListChangeListener<Task>() { -> слушатель изменений в таблице
     * updateCountLabel(); -> выводит количество задач
     * timeCurrent(); -> отображения времени
     * txtTodoList.setOnMouseClicked -> двойное нажатие мыши на строку
     * changeLocaleBox -> слушает изменение языка
     * txtTodoList.setRowFactory -> Слушатель изменяет цвета статуса состояния задачи
     * setChanged(); notifyObservers(selectedLang); -> уведомить всех слушателей, что произошла смена языка
     */
    private void initListeners() {


        taskObservableList.addListener(new ListChangeListener<Task>() {
//        todoListImpl.getTasksList().addListener(new ListChangeListener<Task>() {
            @Override
            public void onChanged(Change<? extends Task> change) {
               // System.out.println("taskObservableList.size()" + taskObservableList.size());
                updateCountLabel();
                timeCurrent();
                //fillTable(); // пробую <---- не работает
               // txtTodoList.refresh(); //пробую
            }
        });


        txtTodoList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    editDialogController.setTaskManagement((Task) txtTodoList.getSelectionModel().getSelectedItem());
                    showDialog();
                    todoListImpl.update((Task) txtTodoList.getSelectionModel().getSelectedItem());
                }
            }
        });


        changeLocaleBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Lang selectedLang = (Lang) changeLocaleBox.getSelectionModel().getSelectedItem();
                ManagerLocale.setCurrentLang(selectedLang);

                setChanged();
                notifyObservers(selectedLang);
            }
        });


        txtTodoList.setRowFactory(row -> new TableRow<Task>() {
            public void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.toString().contains("не выполнена")) {
                    row.getColumns().get(2).setId("error");
                } else {
                    row.getColumns().get(2).setId("not-error");
                }
            }
        });
    }

    /**
     * отображение текущего количество задач
     */
    private void updateCountLabel() {
       // labelCount.setText(resourceBundle.getString("Number_tasks") + ": " + todoListImpl.getTasksList().size());

        labelCount.setText(resourceBundle.getString("Number_tasks") + ": " + taskObservableList.size());
    }

    /**
     * отображение текущего времени
     */
    private void timeCurrent() {
        labelTime.setText(resourceBundle.getString("Time_tasks") + ": " + CurrentTime.currentTime());
    }

    /**
     * (!(source instanceof Button)) -> если нажата не кнопка выходим из метода
     */
    public void actionButtonPressed(ActionEvent event) throws Exception {

        Object source = event.getSource();

        if (!(source instanceof Button)) {
            return;
        }
        Task selectedTask = (Task) txtTodoList.getSelectionModel().getSelectedItem();
        Button clickedButton = (Button) source;

        boolean research = false;

        switch (clickedButton.getId()) {

            case "btnNew":
                Task task = new Task();
//                editDialogController.setTaskManagement(new Task());
                editDialogController.setTaskManagement(task);
                showDialog();
                System.out.println("editDialogController.isSaveClicked() = " + editDialogController.isSaveClicked());

                if (editDialogController.isSaveClicked()) {
                    System.out.println("inner");
                    todoListImpl.add(editDialogController.getTask());
                    research = true;
                }
                // fillTable();
                break;

            case "btnDelete":
                if (!taskIsSelected(selectedTask) || !(confirmDelete())) {
                    return;
                }
                todoListImpl.delete(txtTodoList.getSelectionModel().getSelectedItem());
                research = true;
                break;

            case "btnUpdate":
                if (!taskIsSelected(selectedTask)) {
                    return;
                }
                editDialogController.setTaskManagement((Task) txtTodoList.getSelectionModel().getSelectedItem());
                showDialog();
                if (editDialogController.isSaveClicked()) {
                    todoListImpl.update(selectedTask);
                    research = true;
                }
                break;


            /**
             * меняется статус задачи выполнена или не выполнена
             * txtTodoList.refresh(); -> обновляет состояние таблицы tableview при выборе статуса
             * btn.exit -> выход из программы
             * btn.clear -> очищает поисковое окно
             */
            case "btnComplete":
                if (!taskIsSelected(selectedTask)) {
                    return;
                }
                String unComplete = "не выполнена";
                String complete = "выполнена";
                if (!(txtTodoList.getSelectionModel().getSelectedItem().getStatus().equals("не выполнена"))) {
                    txtTodoList.getSelectionModel().getSelectedItem().setStatus(unComplete);
                    todoListImpl.completeTask(selectedTask, unComplete);
                } else {
                    txtTodoList.getSelectionModel().getSelectedItem().setStatus(complete);
                    todoListImpl.completeTask(selectedTask, complete);
                }
                txtTodoList.refresh();
                research = true;
                break;

            case "btnExit":
                System.exit(1); //выход из программы
                break;

            case "btnClear":
                //txtSearch.clear();
                //txtSearch.getText(txtSearch.clear());
//                setupClearButtonField(txtSearch);
//                for (String line : txtSearch.getText().split("\n")) {
//                    if (line.contains(txtSearch.getText())) {
//                        txtSearch.setText(txtSearch.getText().replace(line, ""));
//                    }
//                }
                ClearTextSearch.clearText(txtSearch);
                research = true;
                break;
        }
      //   fillTable();

        if (research) {
            actionSearch(event);
        }
    }

    /**
     * проверяет выбрана задача или нет, возврашает статус
     * @param task текущая задача(выбрана)
     * @return выбрана задача или нет
     */
    private boolean taskIsSelected(Task task) {
        if (task == null) {
            DialogManager.showInfoDialog(resourceBundle.getString("error"), resourceBundle.getString("select_person"));
            return false;
        }
        return true;
    }

    /**
     * editDialogStage.showAndWait(); -> для ожидания закрытия окна
     */
    private void showDialog() {

        if (editDialogStage == null) {
            editDialogStage = new Stage();
            //editDialogStage.setTitle(resourceBundle.getString("Edit"));
            editDialogStage.setMinHeight(150);
            editDialogStage.setMinWidth(300);
            editDialogStage.setResizable(false);
           // editDialogStage.setScene(new Scene(fxmlEdit));
            editDialogStage.initModality(Modality.WINDOW_MODAL);
           // editDialogStage.initOwner(mainStage);
            editDialogStage.initOwner(changeLocaleBox.getParent().getScene().getWindow()); // трай
        }
        editDialogStage.setScene(new Scene(editView.getView(ManagerLocale.getCurrentLang().getLocale())));
            // editDialogStage.setScene(new Scene(fxmlEdit));

        editDialogStage.setTitle(resourceBundle.getString("Edit"));

        editDialogStage.showAndWait(); // для ожидания закрытия окна



    }

    /**
     * поиск по вхождению текста и по второму столбцу если ввели время или месяц
     * если пустая строка, то возвращается текущий список.
     *
     * @param actionEvent текущее событие
     */
    public void actionSearch(ActionEvent actionEvent) {

        if (txtSearch.getText().trim().length() == 0) {
           // todoListImpl.findAll();
            taskObservableList.clear();
            taskObservableList.addAll(todoListImpl.findAll());
        } else {
           // todoListImpl.find(txtSearch.getText());
            taskObservableList.clear();
            taskObservableList.addAll(todoListImpl.find(txtSearch.getText()));
        }
    }

    /**
     * подтверждает удаление задачи
     * @return удаляем, если нажата кнопка ок, иначе отмена
     */
    private boolean confirmDelete() {
        if (DialogManager.showConfirmDialog(resourceBundle.getString("confirm"), resourceBundle.getString("confirm_delete")).get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }

    }

}




