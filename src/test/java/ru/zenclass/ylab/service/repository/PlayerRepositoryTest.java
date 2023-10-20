package ru.zenclass.ylab.service.repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.liquibase.LiquibaseMigrationRunner;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.PlayerRepositoryImpl;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class PlayerRepositoryTest {

    // Запуск контейнера PostgreSQL перед выполнением тестов
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    private PlayerRepository repository;

    @BeforeEach
    void setUp() {
        // Получаем данные для подключения к контейнеру PostgreSQL
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();
        String driver = postgres.getDriverClassName();

        // Подготовка DatabaseConnectionManager с данными для подключения к тестовой БД
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager(jdbcUrl, username, password,driver);

        // Запуск миграций Liquibase
        LiquibaseMigrationRunner migrationRunner = new LiquibaseMigrationRunner(connectionManager);
        migrationRunner.runMigrations();

        // Инициализация репозитория
        repository = new PlayerRepositoryImpl(connectionManager);
    }

    @Test
    void testAddPlayer() {
        Player player = new Player();
        player.setId(1L);
        player.setUsername("testUser");
        player.setPassword("testPassword");
        player.setBalance(new BigDecimal("100.00"));

        repository.addPlayer(player);

        // Проверяем, что у игрока появился ID после добавления
        assertNotNull(player.getId());
    }

    @Test
    void testFindPlayerByUsername() {
        Player player = new Player();
        player.setUsername("userByUsername");
        player.setPassword("passwordByUsername");
        player.setBalance(new BigDecimal("200.00"));

        repository.addPlayer(player);

        // Пытаемся найти игрока по имени пользователя
        Optional<Player> foundPlayerOpt = repository.findPlayerByUsername(player.getUsername());
        assertTrue(foundPlayerOpt.isPresent());

        Player foundPlayer = foundPlayerOpt.get();
        assertEquals(player.getUsername(), foundPlayer.getUsername());
        assertEquals(player.getPassword(), foundPlayer.getPassword());
        assertEquals(player.getBalance(), foundPlayer.getBalance());
    }
    @Test
    void testFindNonExistentPlayerByUsername() {
        Optional<Player> notFoundPlayerOpt = repository.findPlayerByUsername("nonExistentUsername");
        assertFalse(notFoundPlayerOpt.isPresent());
    }

    @Test
    void testUpdatePlayer() {
        Player player = new Player();
        player.setUsername("testUsername");
        player.setPassword("testPassword");
        player.setBalance(new BigDecimal("300.00"));

        repository.addPlayer(player);

        // Проверяем начальный баланс
        Optional<Player> initialPlayerOpt = repository.findPlayerById(player.getId());
        assertTrue(initialPlayerOpt.isPresent());
        assertEquals(new BigDecimal("300.00"), initialPlayerOpt.get().getBalance());

        // Изменяем баланс и обновляем в базе данных
        player.setBalance(new BigDecimal("400.00"));
        repository.updatePlayer(player);

        // Проверяем обновленный баланс
        Optional<Player> updatedPlayerOpt = repository.findPlayerById(player.getId());
        assertTrue(updatedPlayerOpt.isPresent());
        assertEquals(new BigDecimal("400.00"), updatedPlayerOpt.get().getBalance());
    }
    @Test
    void testFindPlayerById() {
        Player player = new Player();
        player.setId(1L);
        player.setUsername("testUser");
        player.setPassword("testPassword");
        player.setBalance(new BigDecimal("100.00"));

        repository.addPlayer(player);

        Optional<Player> foundPlayerOpt = repository.findPlayerById(player.getId());
        assertTrue(foundPlayerOpt.isPresent());

        Player foundPlayer = foundPlayerOpt.get();
        assertEquals(player.getUsername(), foundPlayer.getUsername());
        assertEquals(player.getPassword(), foundPlayer.getPassword());
        assertEquals(player.getBalance(), foundPlayer.getBalance());
    }
}
