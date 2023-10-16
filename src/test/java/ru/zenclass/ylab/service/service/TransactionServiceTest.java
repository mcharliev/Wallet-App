package ru.zenclass.ylab.service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.liquibase.LiquibaseMigrationRunner;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.model.Transaction;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.TransactionService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class TransactionServiceTest {

    // Запуск контейнера PostgreSQL перед выполнением тестов
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
    private TransactionRepository transactionRepository;
    private PlayerRepository playerRepository;
    private PlayerService playerService;
    private TransactionService transactionService;

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

        // Инициализация репозитория
        transactionRepository = new TransactionRepository(connectionManager);
        playerRepository = new PlayerRepository(connectionManager);
        playerService = new PlayerService(playerRepository);
        transactionService = new TransactionService(transactionRepository,playerService);
    }
    @Test
    public void testAddDebitTransaction() {
        Player player = new Player();
        player.setUsername("testPlayer");
        player.setBalance(new BigDecimal("1000"));
        // Добавляем игрока в базу данных.
        playerRepository.addPlayer(player);


        ByteArrayInputStream in = new ByteArrayInputStream("500\n".getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);

        transactionService.addDebitTransaction(player, scanner);

        Player updatedPlayer = playerService.findPlayerById(player.getId());
        assertEquals(new BigDecimal("500.00"), updatedPlayer.getBalance());
    }

    @Test
    public void testAddCreditTransaction() {
        Player player = new Player();
        player.setUsername("testPlayer");
        player.setBalance(new BigDecimal("1000"));
        playerRepository.addPlayer(player);

        ByteArrayInputStream in = new ByteArrayInputStream("500\n".getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);

        transactionService.addCreditTransaction(player, scanner);

        Player updatedPlayer = playerService.findPlayerById(player.getId());
        assertEquals(new BigDecimal("1500.00"), updatedPlayer.getBalance());
    }

    @Test
    public void testShowPlayerBalance() {
        Player player = new Player();
        player.setUsername("testPlayer");
        player.setBalance(new BigDecimal("1000"));
        playerRepository.addPlayer(player);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        transactionService.showPlayerBalance(player.getId());

        String output = out.toString();
        assertTrue(output.contains("Баланс на счету игрока составляет: 1000"));
    }

    @Test
    public void testViewTransactionHistory() {
        Player player = new Player();
        player.setUsername("testPlayer");
        player.setBalance(new BigDecimal("1000"));
        playerRepository.addPlayer(player);

        transactionService.addCreditTransaction(player, new Scanner(new ByteArrayInputStream("500\n".getBytes())));
        transactionService.addDebitTransaction(player, new Scanner(new ByteArrayInputStream("250\n".getBytes())));

        List<Transaction> transactions = transactionRepository.getAllTransactionsByPlayerId(player.getId());
        assertEquals(2, transactions.size());
    }
}
