package ru.tdd.geo.unit.application.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.tdd.geo.application.utils.TextUtils;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * Набор тестов утилиты по работе со строками
 */
class TextUtilsTest {

    @Test
    void isEmptyTest() {
        boolean actual1 = TextUtils.isEmptyWithNull(null);
        boolean actual2 = TextUtils.isEmptyWithNull("");
        boolean actual3 = TextUtils.isEmptyWithNull("str");

        Assertions.assertTrue(actual1);
        Assertions.assertTrue(actual2);
        Assertions.assertFalse(actual3);
    }
}
