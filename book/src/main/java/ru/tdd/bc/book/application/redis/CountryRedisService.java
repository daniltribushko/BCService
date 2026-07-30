package ru.tdd.bc.book.application.redis;

import ru.tdd.bc.book.database.entities.Country;
import ru.tdd.bc.services.RedisService;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Сервис для работы со странами в redis
 */
public interface CountryRedisService extends RedisService<UUID, Country> {

}
