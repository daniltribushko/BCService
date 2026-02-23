package ru.tdd.author.application.redis;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * @param <K> класс ключа
 * @param <T> класс значения
 */
public interface RedisService<K, T> {

    T get(K key);

    void delete(K key);

    void put(T dto);
}
