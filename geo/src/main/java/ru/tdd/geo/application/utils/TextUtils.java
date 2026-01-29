package ru.tdd.geo.application.utils;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * Утилиты для работы со строками
 */
public class TextUtils {

    private TextUtils() {}

    /** Проверка является ли строка пустой и не равной null*/
    public static boolean isEmptyWithNull(String str) {
        return str == null || str.isEmpty();
    }
}
