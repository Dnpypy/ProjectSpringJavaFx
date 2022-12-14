package ru.springboot.todolist.javafx.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
 * печать всех spring бинов в консоль  printBeans()
 */
@SuppressWarnings("SpringJavaStaticMembersAutowiringInspection")
@Component
@Slf4j
public class MainController extends Observable {

//    private Logger logger = LoggerFactory.getLogger(MainController.class);

    /**
     * @param PAGE_SIZE количество задач на странице
     * @param MAX_PAGE_SHOW количество страниц, которые видны сразу
     * @param page текущие постраничные данные
     */
    private static final int PAGE_SIZE = 12;
    public static final int MAX_PAGE_SHOW = 10;
    private Page page;


    /**
     * Spring JPA реализация
     */
    @Autowired
    private ServiceTaskDao serviceTaskDao;

    ObservableList<Task> taskObservableList;
    @Autowired
    private MainView mainView;

    @Autowired
    private EditView editView;

    @Autowired
    private EditDialogController editDialogController;

    @FXML
    private Pagination pagination;

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
    public TableView<Task>  taskTableViewList;

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
        pagination.setMaxPageIndicatorCount(MAX_PAGE_SHOW);
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
       // System.out.println("fillLangComboBox()");
        log.info("fillLangComboBox()");
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
        updateCountLabel(page.getTotalElements()); // вначале заполняется
        timeCurrent();      // вначале заполняется
    }

    private void fillPagination(Page page) {
        if (page.getTotalPages()<=1){
            pagination.setDisable(true);
        }else {
            pagination.setDisable(false);
        }

        pagination.setPageCount(page.getTotalPages());

        taskObservableList = FXCollections.observableArrayList(page.getContent());
        taskTableViewList.setItems(taskObservableList);
    }

    /**
     * serviceTaskDao.findAll(); заполнение листа из таблицы
     * setItems(list); -> заполнение tableview
     * taskTableViewList.refresh(); -> обновление tableView
     */
    private void fillTable() {
        if (txtSearch.getText().trim().length() == 0) {
            page = serviceTaskDao.findAll(0, PAGE_SIZE);
        }else {
            page = serviceTaskDao.findAll(0, PAGE_SIZE, txtSearch.getText());
        }
        fillPagination(page);
        pagination.setCurrentPageIndex(0);
        updateCountLabel(page.getTotalElements());
        log.info("table loaded....");
    }

    // для показа данных с любой страницы
    private void fillTable(int pageNumber) {
        if (txtSearch.getText().trim().length() == 0) {
            page = serviceTaskDao.findAll(pageNumber, PAGE_SIZE);
        }else {
            page = serviceTaskDao.findAll(pageNumber, PAGE_SIZE, txtSearch.getText());
        }
        fillPagination(page);
        updateCountLabel(page.getTotalElements());

    }

    private void setupClearButtonField(TextField txtSearch) {
        txtSearch.clear();
    }

    /**
     * слушатели событий
     * taskObservableList.addListener(new ListChangeListener<Task>() { -> слушатель изменений в таблице
     * updateCountLabel(); -> выводит количество задач
     * timeCurrent(); -> отображения времени
     * taskTableViewList.setOnMouseClicked -> двойное нажатие мыши на строку
     * changeLocaleBox -> слушает изменение языка
     * taskTableViewList.setRowFactory -> Слушатель изменяет цвета статуса состояния задачи
     * setChanged(); notifyObservers(selectedLang); -> уведомить всех слушателей, что произошла смена языка
     */
    private void initListeners() {


        taskObservableList.addListener(new ListChangeListener<Task>() {
//        serviceTaskDao.getTasksList().addListener(new ListChangeListener<Task>() {
            @Override
            public void onChanged(Change<? extends Task> change) {
               // System.out.println("taskObservableList.size()" + taskObservableList.size());
                updateCountLabel(page.getTotalElements());
                timeCurrent();
                //fillTable(); // пробую <---- не работает
               // taskTableViewList.refresh(); //пробую
            }
        });


        taskTableViewList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    editDialogController.setTaskManagement((Task) taskTableViewList.getSelectionModel().getSelectedItem());
                    showDialog();
                    serviceTaskDao.update((Task) taskTableViewList.getSelectionModel().getSelectedItem());
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


        taskTableViewList.setRowFactory(row -> new TableRow<Task>() {
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

        pagination.currentPageIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                fillTable(newValue.intValue());
            }
        });
    }

    /**
     * отображение текущего количество задач
     */
    private void updateCountLabel(long count) {
       // labelCount.setText(resourceBundle.getString("Number_tasks") + ": " + serviceTaskDao.getTasksList().size());

//        labelCount.setText(resourceBundle.getString("Number_tasks") + ": " + taskObservableList.size());
        labelCount.setText(resourceBundle.getString("Number_tasks") + ": " + count);
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
        Task selectedTask = (Task) taskTableViewList.getSelectionModel().getSelectedItem();
        Button clickedButton = (Button) source;

        boolean research = false;

        switch (clickedButton.getId()) {

            case "btnNew":
                Task task = new Task();
//                editDialogController.setTaskManagement(new Task());
                editDialogController.setTaskManagement(task);
                showDialog();
                //System.out.println("editDialogController.isSaveClicked() = " + editDialogController.isSaveClicked());
                log.info("editDialogController.isSaveClicked() = " + editDialogController.isSaveClicked());

                if (editDialogController.isSaveClicked()) {
                   // System.out.println("inner");
                    log.info("inner");
                    serviceTaskDao.add(editDialogController.getTask());
                    research = true;
                }
                // fillTable();
                break;

            case "btnDelete":
                if (!taskIsSelected(selectedTask) || !(confirmDelete())) {
                    return;
                }
                serviceTaskDao.delete(taskTableViewList.getSelectionModel().getSelectedItem());
                research = true;
                break;

            case "btnUpdate":
                if (!taskIsSelected(selectedTask)) {
                    return;
                }
                editDialogController.setTaskManagement((Task) taskTableViewList.getSelectionModel().getSelectedItem());
                showDialog();
                if (editDialogController.isSaveClicked()) {
                    serviceTaskDao.update(selectedTask);
                    research = true;
                }
                break;


            /**
             * меняется статус задачи выполнена или не выполнена
             * taskTableViewList.refresh(); -> обновляет состояние таблицы tableview при выборе статуса
             * btn.exit -> выход из программы
             * btn.clear -> очищает поисковое окно
             */
            case "btnComplete":
                if (!taskIsSelected(selectedTask)) {
                    return;
                }
                String unComplete = "не выполнена";
                String complete = "выполнена";
                if (!(taskTableViewList.getSelectionModel().getSelectedItem().getStatus().equals("не выполнена"))) {
                    taskTableViewList.getSelectionModel().getSelectedItem().setStatus(unComplete);
                    serviceTaskDao.completeTask(selectedTask, unComplete);
                } else {
                    taskTableViewList.getSelectionModel().getSelectedItem().setStatus(complete);
                    serviceTaskDao.completeTask(selectedTask, complete);
                }
                taskTableViewList.refresh();
               // research = true;
                break;

            case "btnExit":
                System.exit(1);
                break;

            case "btnClear":
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
           // serviceTaskDao.findAll();
            taskObservableList.clear();
            taskObservableList.addAll(serviceTaskDao.findAll());
        } else {
           // serviceTaskDao.find(txtSearch.getText());
            taskObservableList.clear();
            taskObservableList.addAll(serviceTaskDao.find(txtSearch.getText()));
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




