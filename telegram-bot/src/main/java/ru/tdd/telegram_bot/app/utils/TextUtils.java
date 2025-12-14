package ru.tdd.telegram_bot.app.utils;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Утилиты для работы со строками
 */
public class TextUtils {

    /** Проверка является ли строка пустой и не равной null*/
    public static Boolean isEmptyWithNull(String str) {
        return str == null || str.isEmpty();
    }
}
