package ru.zenclass.ylab.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



/**
 * Класс, представляющий сущность "игрок" с информацией о его идентификаторе, имени,
 * пароле, балансе и списке транзакций.
 */
public class Player {
    private long id;                    // Идентификатор игрока
    private String username;              // Имя игрока
    private String password;              // Пароль игрока
    private BigDecimal balance;           // Баланс игрока
    private final List<Transaction> transactions = new ArrayList<>(); // Список транзакций игрока

    /**
     * Получить идентификатор игрока.
     * @return Идентификатор игрока.
     */
    public Long getId() {
        return id;
    }

    /**
     * Установить идентификатор игрока.
     * @param id Идентификатор игрока.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Получить имя игрока.
     * @return Имя игрока.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Установить имя игрока.
     * @param username Имя игрока.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Получить пароль игрока.
     * @return Пароль игрока.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Установить пароль игрока.
     * @param password Пароль игрока.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Получить баланс игрока.
     * @return Баланс игрока.
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Установить баланс игрока.
     * @param balance Баланс игрока.
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * Получить список транзакций игрока.
     * @return Список транзакций игрока.
     */
    public List<Transaction> getTransaction() {
        return transactions;
    }

    /**
     * Добавить транзакцию в список транзакций игрока.
     * @param transaction Транзакция для добавления.
     */
    public void setTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    /**
     * Переопределение метода toString() для представления объекта игрока в виде строки.
     * @return Строковое представление объекта игрока.
     */
    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                '}';
    }
}
