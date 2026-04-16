package ru.tdd.core.application.utils;

/**
 * @author Tribushko Danil
 * @since 14.03.2026
 * Утилита для работы со строками
 */
public class TextUtils {

    private TextUtils() {}

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
