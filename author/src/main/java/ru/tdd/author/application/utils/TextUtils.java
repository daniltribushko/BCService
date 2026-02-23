package ru.tdd.author.application.utils;

/**
 * @author Tribushko Danil
 * @since 19.02.2026
 * Набор утилит для работы с теккстом
 */
public class TextUtils {

    private TextUtils() {}

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNonEmpty(String str) {
        return !isEmpty(str);
    }
}
