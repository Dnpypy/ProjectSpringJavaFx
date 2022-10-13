package ru.springboot.todolist.javafx.utils;



import ru.springboot.todolist.javafx.objects.Lang;

import java.util.Locale;

public class ManagerLocale {

    public static final Locale RU_LOCALE = new Locale("ru");
    public static final Locale EN_LOCALE = new Locale("en");

    /**
     * глобальная переменная, считываю из любого места программы
     */
    private static Lang currentLang;

    public static Lang getCurrentLang(){
        return currentLang;
    }

    public static void setCurrentLang(Lang currentLang){
        ManagerLocale.currentLang = currentLang;
    }
}
