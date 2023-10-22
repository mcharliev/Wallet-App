package ru.zenclass.ylab.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public class AmountDTO {
    @NotNull(message = "Сумма должна быть введена")
    @DecimalMin(value = "0.0", inclusive = false, message = "Сумма должна быть больше 0")
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
