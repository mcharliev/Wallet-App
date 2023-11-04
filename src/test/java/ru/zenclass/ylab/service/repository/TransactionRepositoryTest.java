package ru.zenclass.ylab.service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.enums.TransactionType;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@SpringBootTest
public class TransactionRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PlayerRepository playerRepository;


    private Player player;
    private Transaction transaction1;
    private Transaction transaction2;
    private Long playerId;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.username", postgres::getUsername);
    }

    @BeforeEach
    void setUp() {
        player = new Player();
        playerRepository.addPlayer(player);
        playerId = player.getId();

        transaction1 = new Transaction();
        transaction1.setType(TransactionType.CREDIT);
        transaction1.setAmount(new BigDecimal("100.50"));
        transaction1.setLocalDateTime(LocalDateTime.now());

        transaction2 = new Transaction();
        transaction2.setType(TransactionType.DEBIT);
        transaction2.setAmount(new BigDecimal("200.50"));
        transaction2.setLocalDateTime(LocalDateTime.now());
    }

    @Test
    void testAddTransaction() {
        transactionRepository.addTransaction(transaction1, playerId);
        transactionRepository.addTransaction(transaction2, playerId);

        List<Transaction> transactions = transactionRepository.getAllTransactionsByPlayerId(playerId);
        Transaction foundTransaction1 = transactions.get(0);
        Transaction foundTransaction2 = transactions.get(1);

        assertThat(foundTransaction1.getType()).isEqualTo(TransactionType.CREDIT);
        assertThat(foundTransaction1.getAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
        assertThat(foundTransaction2.getType()).isEqualTo(TransactionType.DEBIT);
        assertThat(foundTransaction2.getAmount()).isEqualByComparingTo(new BigDecimal("200.50"));

        assertThat(foundTransaction1.getId()).isNotNull();
        assertThat(foundTransaction2.getId()).isNotNull();

        assertThat(foundTransaction1.getId()).isNotEqualTo(foundTransaction2.getId());
    }

    @Test
    void testGetAllTransactionsForSpecificPlayer() {
        transactionRepository.addTransaction(transaction1, playerId);
        transactionRepository.addTransaction(transaction2, playerId);

        List<Transaction> transactions = transactionRepository.getAllTransactionsByPlayerId(playerId);
        assertThat(transactions).hasSize(2);
    }

    @Test
    void testNoTransactionsForPlayer() {
        List<Transaction> transactions = transactionRepository.getAllTransactionsByPlayerId(playerId);
        assertThat(transactions).isEmpty();
    }
}
