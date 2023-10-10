package ru.zenclass.ylab.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс, представляющий сущность "транзакция" с информацией о её идентификаторе, типе,
 * сумме и времени выполнения.
 */
public class Transaction {
    private String id;                   // Идентификатор транзакции
    private TransactionType type;        // Тип транзакции (дебетовая или кредитная)
    private BigDecimal amount;           // Сумма транзакции
    private LocalDateTime localDateTime; // Дата и время выполнения транзакции

    /**
     * Получить идентификатор транзакции.
     * @return Идентификатор транзакции.
     */
    public String getId() {
        return id;
    }

    /**
     * Установить идентификатор транзакции.
     * @param id Идентификатор транзакции.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Получить тип транзакции (дебетовая или кредитная).
     * @return Тип транзакции.
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Установить тип транзакции (дебетовая или кредитная).
     * @param type Тип транзакции.
     */
    public void setType(TransactionType type) {
        this.type = type;
    }

    /**
     * Получить сумму транзакции.
     * @return Сумма транзакции.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Установить сумму транзакции.
     * @param amount Сумма транзакции.
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Получить дату и время выполнения транзакции.
     * @return Дата и время выполнения транзакции.
     */
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    /**
     * Установить дату и время выполнения транзакции.
     * @param localDateTime Дата и время выполнения транзакции.
     */
    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    /**
     * Переопределение метода toString() для представления объекта транзакции в виде строки.
     * @return Строковое представление объекта транзакции.
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", localDateTime=" + localDateTime +
                '}';
    }
}
