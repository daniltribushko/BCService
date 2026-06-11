package ru.tdd.bc.services;

/**
 * @author Tribushko Danil
 * @since 09.06.2026
 * @param <K> класс ключа
 * @param <T> класс значения
 *           Сервис для работы с redis
 */
public interface RedisService<K, T> {

    T get(K key);

    void delete(K key);

    void put(T dto);
}
