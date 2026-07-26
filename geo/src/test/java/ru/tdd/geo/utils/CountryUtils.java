package ru.tdd.geo.utils;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 10.07.2026
 * Набор утилит для работы со странами в тестах
 */
public class CountryUtils {

    private CountryUtils() {}

    /**
     * Россия
     */
    public static final UUID COUNTRY_ID1 = UUID.fromString("24b6f080-d895-42b8-955f-1d75236a680e");

    /**
     * Китай
     */
    public static final UUID COUNTRY_ID2 = UUID.fromString("80854a4d-931a-492e-8a6e-a535fe778fa5");

    /**
     * Франция
     */
    public static final UUID COUNTRY_ID3 = UUID.fromString("4c01746d-167a-4c04-b6a9-26af68649834");

    /**
     * Италия
     */
    public static final UUID COUNTRY_ID4 = UUID.fromString("ac843b1f-6ef1-4601-8927-2db8d1771da9");
}
