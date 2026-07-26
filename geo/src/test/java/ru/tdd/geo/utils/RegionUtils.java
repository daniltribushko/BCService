package ru.tdd.geo.utils;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 10.07.2026
 * Набор утилит для работы с регионами в тестах
 */
public class RegionUtils {

    private RegionUtils() {}

    /**
     * Россия - Московская область
     */
    public static final UUID REGION_ID1 = UUID.fromString("bc468b95-8180-4cc2-9f06-541702c10b39");

    /**
     * Россия - Ленинградская область
     */
    public static final UUID REGION_ID2 = UUID.fromString("e2f5877b-4688-43cd-a7fc-373cb5a5166e");

    /**
     * Россия - Свердловская область
     */
    public static final UUID REGION_ID3 = UUID.fromString("ce7dc077-fea8-4bc2-87bd-55696921b71a");

    /**
     * Китай - Аньхой
     */
    public static final UUID REGION_ID4 = UUID.fromString("c26186c7-8782-4c94-83df-0bab868063b8");

    /**
     * Китай - Хэйлунцзян
     */
    public static final UUID REGION_ID5 = UUID.fromString("76ed9fee-9dc0-41c4-b8c6-afe190624a9a");

    /**
     * Италия - Сицилия
     */
    public static final UUID REGION_ID6 = UUID.fromString("bde94e99-fd67-4999-ab47-2ea77b1e4c4f");
}
