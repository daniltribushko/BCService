package ru.tdd.author.application.redis;

import ru.tdd.author.application.dto.countries.CountryDTO;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Сервис для работы со странами в redis
 */
public interface CountryRedisService extends RedisService<UUID, CountryDTO> {

}
