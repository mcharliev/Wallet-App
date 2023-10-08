package ru.zenclass.ylab.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class Player {
    private String id;
    private String username;
    private String password;
    private BigDecimal balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransaction() {
        return transactions;
    }

    public void setTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                '}';
    }
}
