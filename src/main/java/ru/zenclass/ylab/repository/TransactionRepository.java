package ru.zenclass.ylab.repository;

import ru.zenclass.ylab.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для хранения данных о транзакциях.
 */
public class TransactionRepository {
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * Добавляет транзакцию в репозиторий.
     * @param transaction Транзакция для добавления.
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Возвращает список всех транзакций.
     * @return Список всех транзакций.
     */
    public List<Transaction> getAllTransactions(){
        return transactions;
    }
}
