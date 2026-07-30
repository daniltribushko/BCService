package ru.tdd.bc.book.application.redis.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.tdd.bc.book.application.redis.CountryRedisService;
import ru.tdd.bc.book.database.entities.Country;
import ru.tdd.bc.book.database.service.CountryDbService;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 */
@Service
public class CountryRedisServiceImp implements CountryRedisService {

    private final RedisTemplate<String, Country> redisTemplate;

    private final CountryDbService countryService;

    @Autowired
    public CountryRedisServiceImp(
            RedisTemplate<String, Country> redisTemplate,
            CountryDbService countryService
    ) {
        this.redisTemplate = redisTemplate;
        this.countryService = countryService;
    }

    @Override
    public Country get(UUID key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key.toString()))
                .orElseGet(() -> {
                            Country country = countryService.getById(key);
                            redisTemplate.opsForValue().set(
                                    key.toString(),
                                    country,
                                    15,
                                    TimeUnit.MINUTES
                            );
                            return country;
                        }
                );
    }

    @Override
    public void delete(UUID key) {
        redisTemplate.delete(key.toString());
    }

    @Override
    public void put(Country country) {
        redisTemplate.opsForValue().set(
                country.getId().toString(),
                country,
                1,
                TimeUnit.HOURS
        );
    }
}

