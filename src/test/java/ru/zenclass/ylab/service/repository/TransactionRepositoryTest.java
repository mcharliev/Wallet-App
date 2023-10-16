package ru.zenclass.ylab.service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.liquibase.LiquibaseMigrationRunner;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.model.Transaction;
import ru.zenclass.ylab.model.TransactionType;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class TransactionRepositoryTest {

    // Запуск контейнера PostgreSQL перед выполнением тестов
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
    private TransactionRepository transactionRepository;
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

        // Инициализация репозитория
        transactionRepository = new TransactionRepository(connectionManager);
        playerRepository = new PlayerRepository(connectionManager);
    }

    @Test
    void testAddTransaction() {
        Transaction transaction1 = new Transaction();
        transaction1.setType(TransactionType.CREDIT);
        transaction1.setAmount(new BigDecimal("100.50"));
        transaction1.setLocalDateTime(LocalDateTime.now());

        Transaction transaction2 = new Transaction();
        transaction2.setType(TransactionType.DEBIT);
        transaction2.setAmount(new BigDecimal("200.50"));
        transaction2.setLocalDateTime(LocalDateTime.now());

        Player player = new Player();
        Long playerId = 3L;
        playerRepository.addPlayer(player);

        transactionRepository.addTransaction(transaction1, playerId);
        transactionRepository.addTransaction(transaction2, playerId);

        List<Transaction> transactions = transactionRepository.getAllTransactionsByPlayerId(playerId);
        // Проверка содержания каждой транзакции
        Transaction foundTransaction1 = transactions.get(0);
        Transaction foundTransaction2 = transactions.get(1);

        assertEquals(TransactionType.CREDIT, foundTransaction1.getType());
        assertEquals(new BigDecimal("100.50"), foundTransaction1.getAmount());
        assertEquals(TransactionType.DEBIT, foundTransaction2.getType());
        assertEquals(new BigDecimal("200.50"), foundTransaction2.getAmount());

        // Проверка наличия ID у каждой транзакции
        assertNotNull(foundTransaction1.getId());
        assertNotNull(foundTransaction2.getId());

        // Проверка уникальности ID каждой транзакции
        assertNotEquals(foundTransaction1.getId(), foundTransaction2.getId());
    }

    @Test
    void testGetAllTransactionsForSpecificPlayer() {
        Player player = new Player();
        playerRepository.addPlayer(player);
        Long playerId = player.getId(); // Предположим, что у вас есть такой метод

        Transaction transaction1 = new Transaction();
        transaction1.setType(TransactionType.CREDIT);
        transaction1.setAmount(new BigDecimal("100.50"));
        transaction1.setLocalDateTime(LocalDateTime.now());
        transactionRepository.addTransaction(transaction1, playerId);

        Transaction transaction2 = new Transaction();
        transaction2.setType(TransactionType.DEBIT);
        transaction2.setAmount(new BigDecimal("200.50"));
        transaction2.setLocalDateTime(LocalDateTime.now());
        transactionRepository.addTransaction(transaction2, playerId);

        List<Transaction> transactions = transactionRepository.getAllTransactionsByPlayerId(playerId);
        assertEquals(2, transactions.size());
    }

    @Test
    void testNoTransactionsForPlayer() {
        Player player = new Player();
        playerRepository.addPlayer(player);
        Long playerId = player.getId();

        List<Transaction> transactions = transactionRepository.getAllTransactionsByPlayerId(playerId);
        assertTrue(transactions.isEmpty());
    }
}
