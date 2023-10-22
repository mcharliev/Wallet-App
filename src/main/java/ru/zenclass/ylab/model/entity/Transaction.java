package ru.zenclass.ylab.model.entity;


import ru.zenclass.ylab.model.enums.TransactionType;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) && type == that.type && Objects.equals(amount, that.amount) && Objects.equals(localDateTime, that.localDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, amount, localDateTime);
    }
}
