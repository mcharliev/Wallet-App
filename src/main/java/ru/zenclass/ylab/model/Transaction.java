package ru.zenclass.ylab.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

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
