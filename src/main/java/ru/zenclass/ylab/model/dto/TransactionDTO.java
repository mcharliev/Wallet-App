package ru.zenclass.ylab.model.dto;

import lombok.Data;
import ru.zenclass.ylab.model.enums.TransactionType;

import java.math.BigDecimal;

@Data
public class TransactionDTO {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private String localDateTime;
}
