package ru.zenclass.ylab.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс, представляющий сущность "транзакция" с информацией о её идентификаторе, типе,
 * сумме и времени выполнения.
 */
public class Transaction {
    private Long id;                   // Идентификатор транзакции
    private TransactionType type;        // Тип транзакции (дебетовая или кредитная)
    private BigDecimal amount;           // Сумма транзакции
    private LocalDateTime localDateTime; // Дата и время выполнения транзакции

    /**
     * Получить идентификатор транзакции.
     * @return Идентификатор транзакции.
     */
    public long getId() {
        return id;
    }

    /**
     * Установить идентификатор транзакции.
     * @param id Идентификатор транзакции.
     */
    public void setId(long id) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id && type == that.type && Objects.equals(amount, that.amount) && Objects.equals(localDateTime, that.localDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, amount, localDateTime);
    }
}
