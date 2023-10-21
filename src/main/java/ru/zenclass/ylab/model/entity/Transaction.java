package ru.zenclass.ylab.model.entity;

import lombok.*;
import ru.zenclass.ylab.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс, представляющий сущность "транзакция" с информацией о её идентификаторе, типе,
 * сумме и времени выполнения.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class Transaction {
    private Long id;                   // Идентификатор транзакции
    private TransactionType type;        // Тип транзакции (дебетовая или кредитная)
    private BigDecimal amount;           // Сумма транзакции
    private LocalDateTime localDateTime; // Дата и время выполнения транзакции
}
