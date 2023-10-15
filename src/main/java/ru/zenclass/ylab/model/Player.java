package ru.zenclass.ylab.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Класс, представляющий сущность "игрок" с информацией о его идентификаторе, имени,
 * пароле, балансе и списке транзакций.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class Player {
    private Long id;                    // Идентификатор игрока
    private String username;              // Имя игрока
    private String password;              // Пароль игрока
    private BigDecimal balance;           // Баланс игрока
    private final List<Transaction> transactions = new ArrayList<>(); // Список транзакций игрока

}
