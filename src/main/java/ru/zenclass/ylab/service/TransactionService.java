package ru.zenclass.ylab.service;

import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    Transaction addDebitTransaction(Player player, BigDecimal debitAmount);

    Transaction addCreditTransaction(Player player, BigDecimal creditAmount);

    List<Transaction> viewTransactionHistory(Long id, String username);

    void showPlayerBalance(Long id);
}
