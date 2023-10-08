package ru.zenclass.ylab.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.zenclass.ylab.exception.NotEnoughMoneyException;
import ru.zenclass.ylab.exception.TransactionIdIsNotUniqueException;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.model.Transaction;
import ru.zenclass.ylab.model.TransactionType;
import ru.zenclass.ylab.repository.TransactionRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    private TransactionService transactionService;
    private TransactionRepository transactionRepository;
    private PlayerService playerService;

    @BeforeEach
    public void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        playerService = mock(PlayerService.class);
        transactionService = new TransactionService(transactionRepository, playerService);
    }

    @Test
    public void testAddDebitTransactionWithValidAmount() {
        //Создаем игрока и сетим значения
        Player player = new Player();
        player.setUsername("TestPlayer");
        player.setBalance(BigDecimal.valueOf(100));

        //Создаем транзакцию  и сетим значения
        Transaction transaction = new Transaction();
        transaction.setId("unique_id");
        transaction.setType(TransactionType.DEBIT);
        transaction.setAmount(BigDecimal.valueOf(50));

        when(playerService.findPlayerById(anyString())).thenReturn(player);
        when(transactionRepository.getAllTransactions()).thenReturn(Collections.emptyList());

        //Создаем строку которая будет имитировать ввод пользователя
        String input = "50\n";

        //Создаем поток ввода, который будет читать байты из строки input
        InputStream in = new ByteArrayInputStream(input.getBytes());

        //Изменяем стандартный ввод (System.in) на ByteArrayInputStream (in)
        System.setIn(in);

        //Scanner теперь читает данные моей имитации, а не из реального стандартного ввода.
        Scanner scanner = new Scanner(System.in);

        transactionService.addDebitTransaction(player, scanner, "unique_id");

        assertEquals(BigDecimal.valueOf(50), player.getBalance());
        assertEquals("unique_id", player.getTransaction().get(0).getId());
    }


    @Test
    public void testAddDebitTransactionWithInsufficientBalance() {
        Player player = new Player();
        player.setBalance(BigDecimal.valueOf(50));

        String input = "100\n";

        InputStream in = new ByteArrayInputStream(input.getBytes());

        System.setIn(in);

        Scanner scanner = new Scanner(System.in);

        assertThrows(NotEnoughMoneyException.class, () -> {
            transactionService.addDebitTransaction(player, scanner, "unique_id");
        });
    }


    @Test
    public void testAddCreditTransaction() {
        Player player = new Player();
        player.setBalance(BigDecimal.valueOf(100));

        Transaction transaction = new Transaction();
        transaction.setId("unique_id");
        transaction.setType(TransactionType.CREDIT);
        transaction.setAmount(BigDecimal.valueOf(50));

        when(playerService.findPlayerById(anyString())).thenReturn(player);
        when(transactionRepository.getAllTransactions()).thenReturn(Collections.emptyList());

        String input = "50\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);

        transactionService.addCreditTransaction(player, scanner, "unique_id");

        assertEquals(BigDecimal.valueOf(150), player.getBalance());
        assertEquals("unique_id", player.getTransaction().get(0).getId());
    }

    @Test
    public void testAddCreditTransactionWithNonUniqueTransactionId() {
        Player player = new Player();
        player.setBalance(BigDecimal.valueOf(100));

        Transaction transaction = new Transaction();
        transaction.setId("unique_id");
        transaction.setType(TransactionType.CREDIT);
        transaction.setAmount(BigDecimal.valueOf(50));

        when(playerService.findPlayerById(anyString())).thenReturn(player);
        when(transactionRepository.getAllTransactions()).thenReturn(Collections.singletonList(transaction));

        String input = "50\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);

        // Ожидается, что этот метод вызовет исключение TransactionIdIsNotUniqueException,
        // так как идентификатор транзакции "unique_id" уже используется в имитации репозитория.
        assertThrows(TransactionIdIsNotUniqueException.class, () -> {
            transactionService.addCreditTransaction(player, scanner, "unique_id");
        });
    }

    @Test
    public void testShowPlayerBalance() {
        Player player = new Player();
        player.setId("player_id");
        player.setUsername("testuser");
        player.setBalance(BigDecimal.valueOf(100));

        when(playerService.findPlayerById(anyString())).thenReturn(player);

        transactionService.showPlayerBalance("player_id");
    }
}