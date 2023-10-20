package ru.zenclass.ylab.model.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.zenclass.ylab.model.entity.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDTO {
    private Long id;                    // Идентификатор игрока
    private String username;              // Имя игрока
    private BigDecimal balance;           // Баланс игрока
    private String password;
    private final List<Transaction> transactions = new ArrayList<>(); // Список транзакций игрока

    /**
     * Добавить транзакцию в список транзакций игрока.
     * @param transaction Транзакция для добавления.
     */
    public void setTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
