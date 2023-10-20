package ru.zenclass.ylab.service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.PlayerServiceImpl;
import ru.zenclass.ylab.service.TransactionService;
import ru.zenclass.ylab.service.TransactionServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private PlayerService playerService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        playerService = Mockito.mock(PlayerServiceImpl.class);
        transactionService = new TransactionServiceImpl(transactionRepository, playerService);
    }

    @Test
    public void testAddDebitTransaction() {
        Player player = new Player();
        player.setUsername("testPlayer");
        player.setBalance(new BigDecimal("1000"));

        Mockito.when(playerService.findPlayerById(player.getId())).thenReturn(player);

        transactionService.addDebitTransaction(player, new BigDecimal("500.00"));

        Player updatedPlayer = playerService.findPlayerById(player.getId());
        assertEquals(new BigDecimal("500.00"), updatedPlayer.getBalance());
    }

    @Test
    public void testAddCreditTransaction() {
        Player player = new Player();
        player.setUsername("testPlayer");
        player.setBalance(new BigDecimal("1000"));

        Mockito.when(playerService.findPlayerById(player.getId())).thenReturn(player);

        transactionService.addCreditTransaction(player, new BigDecimal("500.00"));

        Player updatedPlayer = playerService.findPlayerById(player.getId());
        assertEquals(new BigDecimal("1500.00"), updatedPlayer.getBalance());
    }

    @Test
    public void testViewTransactionHistory() {
        Player player = new Player();
        player.setUsername("testPlayer");
        player.setBalance(new BigDecimal("1000"));

        List<Transaction> transactionsList = Arrays.asList(new Transaction(), new Transaction());

        Mockito.when(transactionRepository.getAllTransactionsByPlayerId(player.getId())).thenReturn(transactionsList);

        List<Transaction> transactions = transactionRepository.getAllTransactionsByPlayerId(player.getId());
        assertEquals(2, transactions.size());
    }

    @Test
    public void testShowPlayerBalance() {
        Player player = new Player();
        player.setUsername("testPlayer");
        player.setBalance(new BigDecimal("1000"));

        Mockito.when(playerService.findPlayerById(player.getId())).thenReturn(player);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        transactionService.showPlayerBalance(player.getId());

        String output = out.toString();
        assertTrue(output.contains("Баланс на счету игрока составляет: 1000"));
    }
}
