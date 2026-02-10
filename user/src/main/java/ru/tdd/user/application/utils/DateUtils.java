package ru.tdd.user.application.utils;

import java.util.Date;

/**
 * @author Tribushko Danil
 * @since 01.02.2026
 * Набор утилит для работы с датой
 */
public class DateUtils {

    public static final long SECUND = 1000;

    public static final long MINUTE = 60 * SECUND;

    public static final long HOUR = 60 * MINUTE;

    public static final long DAY = 24 * HOUR;

    private DateUtils(){}

    public static Date plusTime(Date date1, long time) {
        return new Date(date1.getTime() + time);
    }

    public static boolean isFuture(Date date) {
        return date.after(new Date());
    }
}
