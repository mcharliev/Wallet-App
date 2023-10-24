package ru.zenclass.ylab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.aop.annotations.Loggable;
import ru.zenclass.ylab.exception.NotEnoughMoneyException;
import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.enums.TransactionType;
import ru.zenclass.ylab.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для обработки транзакций.
 */
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final PlayerService playerService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, PlayerService playerService) {
        this.transactionRepository = transactionRepository;
        this.playerService = playerService;
    }

    public Transaction addDebitTransaction(Player player, BigDecimal debitAmount) {
        if (debitAmount.compareTo(player.getBalance()) <= 0) {
            Transaction transaction = new Transaction();
            transaction.setId(1L);
            transaction.setType(TransactionType.DEBIT);
            transaction.setAmount(debitAmount);
            transaction.setLocalDateTime(LocalDateTime.now());
            Transaction savedTransaction = transactionRepository.addTransaction(transaction, player.getId());
            BigDecimal newBalance = player.getBalance().subtract(debitAmount);
            player.setBalance(newBalance);
            player.setTransaction(transaction);
            playerService.updatePlayer(player);
            return savedTransaction;
        } else {
            throw new NotEnoughMoneyException();
        }
    }

    public Transaction addCreditTransaction(Player player, BigDecimal creditAmount) {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.CREDIT);
        transaction.setAmount(creditAmount);
        transaction.setLocalDateTime(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.addTransaction(transaction, player.getId());
        BigDecimal newBalance = player.getBalance().add(creditAmount);
        player.setBalance(newBalance);
        player.setTransaction(transaction);
        playerService.updatePlayer(player);
        return savedTransaction;
    }

    public List<Transaction> viewTransactionHistory(Long id, String username) {
        return transactionRepository.getAllTransactionsByPlayerId(id);
    }
}








