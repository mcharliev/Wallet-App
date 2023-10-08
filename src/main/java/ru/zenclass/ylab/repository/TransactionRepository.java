package ru.zenclass.ylab.repository;

import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getAllTransactions(){
        return transactions;
    }

}
