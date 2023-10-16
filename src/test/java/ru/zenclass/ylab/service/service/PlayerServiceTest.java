package ru.zenclass.ylab.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.exception.PlayerAlreadyExistException;
import ru.zenclass.ylab.liquibase.LiquibaseMigrationRunner;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.service.PlayerService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
public class PlayerServiceTest {

    // Запуск контейнера PostgreSQL перед выполнением тестов
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    private PlayerService playerService;
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        // Получаем данные для подключения к контейнеру PostgreSQL
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();
        String driver = postgres.getDriverClassName();

        // Подготовка DatabaseConnectionManager с данными для подключения к тестовой БД
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager(jdbcUrl, username, password, driver);

        // Запуск миграций Liquibase
        LiquibaseMigrationRunner migrationRunner = new LiquibaseMigrationRunner(connectionManager);
        migrationRunner.runMigrations();

        // Инициализация репозитория и сервиса
        playerRepository = new PlayerRepository(connectionManager);
        playerService = new PlayerService(playerRepository);
    }

    @Test
    void testFindPlayerById() {
        Player player = new Player();
        player.setUsername("testUser");
        player.setPassword("testPassword");
        player.setBalance(new BigDecimal("100.00"));

        playerRepository.addPlayer(player);

        Player foundPlayer = playerService.findPlayerById(player.getId());
        assertEquals(player.getUsername(), foundPlayer.getUsername());
    }

    @Test
    void testLogin() {
        Player player = new Player();
        player.setUsername("loginUser");
        player.setPassword("loginPassword");
        player.setBalance(new BigDecimal("100.00"));

        playerRepository.addPlayer(player);

        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("loginUser", "loginPassword");
        Player loggedInPlayer = playerService.login(new Player(), scanner);

        assertEquals(player.getUsername(), loggedInPlayer.getUsername());
        assertEquals(player.getPassword(), loggedInPlayer.getPassword());
        assertEquals(player.getBalance(), loggedInPlayer.getBalance());
    }

    @Test
    void testRegisterPlayerWithExistingUsername() {
        Player existingPlayer = new Player();
        existingPlayer.setUsername("existingUser");
        existingPlayer.setPassword("existingPassword");
        playerRepository.addPlayer(existingPlayer);

        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("existingUser", "anotherPassword");

        // Проверяем, что при попытке регистрации пользователя с существующим именем выводится нужное сообщение
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        playerService.registerPlayer(scanner);

        String expectedOutput = "Пользователь с таким именем уже существует. Пожалуйста, выберите другое имя.";
        assertTrue(outContent.toString().contains(expectedOutput));
    }
    @Test
    void testUnsuccessfulLogin() {
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("nonexistentUser", "wrongPassword");

        // Проверяем, что при попытке авторизации с неверными данными выводится нужное сообщение
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        playerService.login(new Player(), scanner);

        String expectedOutput = "Ошибка авторизации. Пожалуйста, проверьте введенные данные.";
        assertTrue(outContent.toString().contains(expectedOutput));
    }

    @Test
    void testUpdatePlayer() {
        Player player = new Player();
        player.setUsername("updateUser");
        player.setPassword("updatePassword");
        player.setBalance(new BigDecimal("100.00"));

        playerRepository.addPlayer(player);

        player.setBalance(new BigDecimal("150.00"));
        playerService.updatePlayer(player);

        Optional<Player> updatedPlayerOpt = playerRepository.findPlayerById(player.getId());
        assertTrue(updatedPlayerOpt.isPresent());
        assertEquals(new BigDecimal("150.00"), updatedPlayerOpt.get().getBalance());
    }
}
