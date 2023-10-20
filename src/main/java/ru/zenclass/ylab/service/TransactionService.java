package ru.zenclass.ylab.service;

import ru.zenclass.ylab.model.entity.Player;

import java.math.BigDecimal;

public interface TransactionService {
    void addDebitTransaction(Player player, BigDecimal debitAmount);

    void addCreditTransaction(Player player, BigDecimal creditAmount);

    void viewTransactionHistory(Long id, String username);

    void showPlayerBalance(Long id);
}
