package ru.zenclass.ylab.service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.liquibase.LiquibaseMigrationRunner;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.enums.TransactionType;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.PlayerRepositoryImpl;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.repository.TransactionRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class TransactionRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
    private TransactionRepository transactionRepository;
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();
        String driver = postgres.getDriverClassName();

        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager(jdbcUrl, username, password, driver);

        LiquibaseMigrationRunner migrationRunner = new LiquibaseMigrationRunner(connectionManager);
        migrationRunner.runMigrations();

        transactionRepository = new TransactionRepositoryImpl(connectionManager);
        playerRepository = new PlayerRepositoryImpl(connectionManager);
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
        Transaction foundTransaction1 = transactions.get(0);
        Transaction foundTransaction2 = transactions.get(1);

        assertEquals(TransactionType.CREDIT, foundTransaction1.getType());
        assertEquals(new BigDecimal("100.50"), foundTransaction1.getAmount());
        assertEquals(TransactionType.DEBIT, foundTransaction2.getType());
        assertEquals(new BigDecimal("200.50"), foundTransaction2.getAmount());

        assertNotNull(foundTransaction1.getId());
        assertNotNull(foundTransaction2.getId());

        assertNotEquals(foundTransaction1.getId(), foundTransaction2.getId());
    }

    @Test
    void testGetAllTransactionsForSpecificPlayer() {
        Player player = new Player();
        playerRepository.addPlayer(player);
        Long playerId = player.getId();

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
