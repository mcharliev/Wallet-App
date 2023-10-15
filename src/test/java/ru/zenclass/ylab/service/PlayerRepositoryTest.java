package ru.zenclass.ylab.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.liquibase.LiquibaseMigrationRunner;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.repository.PlayerRepository;

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
        repository = new PlayerRepository(connectionManager);
    }
    @Test
    void testAddAndFindPlayer() {
        Player player = new Player();
        player.setId(1L);
        player.setUsername("testUser");
        player.setPassword("testPassword");
        player.setBalance(new BigDecimal("100.00"));

        repository.addPlayer(player);

        // Проверяем, что у игрока появился ID после добавления
        assertNotNull(player.getId());

        // Пытаемся найти игрока по ID
        Optional<Player> foundPlayerOpt = repository.findPlayerById(player.getId());
        assertTrue(foundPlayerOpt.isPresent());

        Player foundPlayer = foundPlayerOpt.get();
        assertEquals(player.getUsername(), foundPlayer.getUsername());
        assertEquals(player.getPassword(), foundPlayer.getPassword());
        assertEquals(player.getBalance(), foundPlayer.getBalance());
    }
}
