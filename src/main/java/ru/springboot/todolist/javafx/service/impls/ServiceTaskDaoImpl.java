package ru.springboot.todolist.javafx.service.impls;


import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.springboot.todolist.javafx.entity.Task;
import ru.springboot.todolist.javafx.repository.TaskRepository;
import ru.springboot.todolist.javafx.service.ServiceTaskDao;

@SuppressWarnings("deprecation")
@Service
public class ServiceTaskDaoImpl implements ServiceTaskDao {


    @Autowired
    TaskRepository taskRepository;

//    @Autowired
//    private ServiceTaskDao serviceTaskDao;

    @Override
    public boolean completeTask(Task task, String status) {
        task.setStatus(status); // <--- пробую тут вариант
        //task.update(task);
        taskRepository.save(task);   // <--- пробую тут вариант
        return false;
    }

    @Override
    public void add(Task task) {
        taskRepository.save(task);
    }

    @Override
    public void update(Task task) {
        taskRepository.save(task);
    }

    @Override
    public void delete(Task task) {
        taskRepository.delete(task);
    }

    /**
     * методом tasksclear() проверяем очистку таблицы, чтобы не было двойного заполнения таблицы, очищаем текущую.
     * tasks.addAll -> из класса HibernateSessionFactoryUtil получаем ссесию фактори и открываем сессию,
     * указываю сущность моего класса из таблицы и кладем ее в лист
     * findAll() возвращает Iterable<T> благодаря библиотеке com.google.guava:guava:+ ;
     */
    public ObservableList<Task> findAll() {
        System.out.println("findAll =========================");
        return FXCollections.observableArrayList(Lists.newArrayList(taskRepository.findAll()));

    }


    /**
     * поиск задачи по слову
     */

    @Override
    public ObservableList<Task> find(String text) {
        return FXCollections.observableArrayList(Lists.newArrayList(taskRepository.findByTaskContainingIgnoreCase(text)));
    }

    /**
     *
     * @param from с какой страницы начинать
     * @param count какое количество брать
     * @return возвращает все записи с возможностью постраничности
     * Sort.Direction.ASC возможность сортировки
     * "task" по какому полю
     */
    @Override
    public Page findAll(int from, int count) {
        return taskRepository.findAll(new PageRequest(from, count, Sort.Direction.ASC, "task"));
    }

    /**
     *
     * @param from с какой страницы начинать
     * @param count какое количество брать
     * @param text текст который ищем по колонке task
     * @return возвращает все записи с возможностью постраничности
     */
    @Override
    public Page findAll(int from, int count, String... text) {
        return taskRepository.findByTaskContainingIgnoreCase(text[0], new PageRequest(from, count, Sort.Direction.ASC, "task"));
    }
}
