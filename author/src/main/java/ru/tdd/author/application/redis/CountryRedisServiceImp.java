package ru.tdd.author.application.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.application.services.CountryService;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 */
@Service
public class CountryRedisServiceImp implements CountryRedisService {

    private final RedisTemplate<String, CountryDTO> redisTemplate;

    private final CountryService countryService;

    @Autowired
    public CountryRedisServiceImp(
            RedisTemplate<String, CountryDTO> redisTemplate,
            CountryService countryService
    ) {
        this.redisTemplate = redisTemplate;
        this.countryService = countryService;
    }

    @Override
    public CountryDTO get(UUID key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key.toString()))
                .orElseGet(() -> {
                            CountryDTO countryDTO = countryService.getById(key);
                            redisTemplate.opsForValue().set(
                                    key.toString(),
                                    countryDTO,
                                    15,
                                    TimeUnit.MINUTES
                            );
                            return countryDTO;
                        }
                );
    }

    @Override
    public void delete(UUID key) {
        redisTemplate.delete(key.toString());
    }

    @Override
    public void put(CountryDTO dto) {
        redisTemplate.opsForValue().set(
                dto.getId().toString(),
                dto,
                15,
                TimeUnit.MINUTES
        );
    }
}
