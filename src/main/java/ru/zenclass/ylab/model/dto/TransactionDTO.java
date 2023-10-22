package ru.zenclass.ylab.model.dto;


import ru.zenclass.ylab.model.enums.TransactionType;

import java.math.BigDecimal;


public class TransactionDTO {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private String localDateTime;

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

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }
}
