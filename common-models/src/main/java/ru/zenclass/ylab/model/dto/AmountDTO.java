package ru.zenclass.ylab.model.dto;



//import io.swagger.v3.oas.annotations.media.Schema;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Класс, представляющий собой объект передачи данных (DTO) для суммы.
 */
public class AmountDTO {
    @NotNull(message = "Сумма должна быть введена")
    @DecimalMin(value = "0.0", inclusive = false, message = "Сумма должна быть больше 0")
    @Schema(description = "Сумма транзакции", example = "100")
    private BigDecimal amount;

    /**
     * Получить значение суммы.
     * @return сумма в виде объекта {@link BigDecimal}
     */
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
