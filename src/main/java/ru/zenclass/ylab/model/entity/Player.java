package ru.zenclass.ylab.model.entity;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Класс, представляющий сущность "игрок" с информацией о его идентификаторе, имени,
 * пароле, балансе и списке транзакций.
 */

public class Player {
    private Long id;                    // Идентификатор игрока
    private String username;              // Имя игрока
    private String password;              // Пароль игрока
    private BigDecimal balance;           // Баланс игрока
    private final List<Transaction> transactions = new ArrayList<>(); // Список транзакций игрока


    public void setTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
