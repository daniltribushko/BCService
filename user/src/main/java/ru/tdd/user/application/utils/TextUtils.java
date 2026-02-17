package ru.tdd.user.application.utils;

/**
 * @author Tribushko Danil
 * @since 29.01.2026
 * Утилиты для работы с текстом
 */
public class TextUtils {

    private TextUtils(){}

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
