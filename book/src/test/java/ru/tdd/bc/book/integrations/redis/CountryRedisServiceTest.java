package ru.tdd.bc.book.integrations.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.book.TestcontainersConfiguration;
import ru.tdd.bc.book.application.redis.CountryRedisService;
import ru.tdd.bc.book.database.entities.Country;
import ru.tdd.bc.book.sql.InitCountriesSqlScripts;
import ru.tdd.bc.book.utils.CountryUtils;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 23.02.2026
 */
@Testcontainers
@SpringBootTest
@InitCountriesSqlScripts
@Import(value = TestcontainersConfiguration.class)
@DisplayName("Тестирование сервиса по работе со странами в редис")
class CountryRedisServiceTest {

    @Autowired
    private RedisTemplate<String, Country> redisTemplate;

    @Autowired
    private CountryRedisService countryRedisService;

    @BeforeEach
    void cleanDb() {
        Assertions.assertNotNull(redisTemplate.getConnectionFactory());
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    @Test
    @DisplayName("Получение из редиса, когда в редисе нету страны, но она есть в бд")
    void getFromRepositoryTest() {
        Country actual = countryRedisService.get(CountryUtils.COUNTRY_ID3);

        Assertions.assertEquals(CountryUtils.COUNTRY_ID3, actual.getId());
        Assertions.assertEquals("Япония", actual.getName());
    }

    @Test
    @DisplayName("Получение из редиса")
    void getFromRedisTest() {
        Country country =  new Country(UUID.randomUUID(), "Россия");

        redisTemplate.opsForValue().set(country.getId().toString(), country);

        Country actual = countryRedisService.get(country.getId());

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

        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), actual.getMessage());
    }

    @Test
    @DisplayName("Добавление в редис")
    void putInRedisTest() {
        Country country = new Country(UUID.randomUUID(), "Китай");
        countryRedisService.put(country);

        Country actual = countryRedisService.get(country.getId());

        Assertions.assertEquals(country.getId(), actual.getId());
        Assertions.assertEquals("Китай", actual.getName());
    }

    @Test
    @DisplayName("Удаление из редиса")
    void deleteFromRedisTest() {
        Country country = new Country(UUID.randomUUID(), "Китай");
        countryRedisService.put(country);

        countryRedisService.delete(country.getId());

        Country actual = redisTemplate.opsForValue().get(country.getId().toString());

        Assertions.assertNull(actual);
    }
}
