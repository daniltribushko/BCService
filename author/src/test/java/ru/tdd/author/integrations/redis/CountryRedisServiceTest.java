package ru.tdd.author.integrations.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.author.TestcontainersConfiguration;
import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.application.exceptions.country.CountryByIdNotFoundException;
import ru.tdd.author.application.redis.CountryRedisService;
import ru.tdd.author.database.entitites.Country;
import ru.tdd.author.database.repositories.CountryRepository;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 23.02.2026
 */
@Testcontainers
@SpringBootTest
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@DisplayName("Тестирование сервиса по работе со странами в редис")
class CountryRedisServiceTest {

    private final RedisTemplate<String, CountryDTO> redisTemplate;

    private final CountryRedisService countryRedisService;

    private final CountryRepository countryRepository;

    @Autowired
    public CountryRedisServiceTest(
            RedisTemplate<String, CountryDTO> redisTemplate,
            CountryRedisService countryRedisService,
            CountryRepository countryRepository
    ) {
        this.redisTemplate = redisTemplate;
        this.countryRedisService = countryRedisService;
        this.countryRepository = countryRepository;
    }

    @BeforeEach
    void cleanDb() {
        Assertions.assertNotNull(redisTemplate.getConnectionFactory());
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    @Test
    @DisplayName("Получение из редиса, когда в редисе нету страны, но она есть в бд")
    void getFromRepositoryTest() {
        Country country = new Country("Россия");

        countryRepository.save(country);

        CountryDTO actual = countryRedisService.get(country.getId());

        Assertions.assertEquals(country.getId(), actual.getId());
        Assertions.assertEquals("Россия", actual.getName());
    }

    @Test
    @DisplayName("Получение из редиса")
    void getFromRedisTest() {
        CountryDTO country =  new CountryDTO(UUID.randomUUID(), "Россия");

        redisTemplate.opsForValue().set(country.getId().toString(), country);

        CountryDTO actual = countryRedisService.get(country.getId());

        Assertions.assertEquals(country.getId(), actual.getId());
        Assertions.assertEquals("Россия", actual.getName());
    }

    @Test
    @DisplayName("Получение из редиса отсутствующего объекта")
    void getFromRedisNotFoundTest() {
        UUID countryId = UUID.randomUUID();
        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> countryRedisService.get(countryId)
        );

        Assertions.assertEquals("Страна с идентификатором: " + countryId + " не найдена", actual.getMessage());
    }

    @Test
    @DisplayName("Добавление в редис")
    void putInRedisTest() {
        CountryDTO country = new CountryDTO(UUID.randomUUID(), "Китай");
        countryRedisService.put(country);

        CountryDTO actual = countryRedisService.get(country.getId());

        Assertions.assertEquals(country.getId(), actual.getId());
        Assertions.assertEquals("Китай", actual.getName());
    }

    @Test
    @DisplayName("Удаление из редиса")
    void deleteFromRedisTest() {
        CountryDTO country = new CountryDTO(UUID.randomUUID(), "Китай");
        countryRedisService.put(country);

        countryRedisService.delete(country.getId());

        CountryDTO actual = redisTemplate.opsForValue().get(country.getId().toString());

        Assertions.assertNull(actual);
    }
}
