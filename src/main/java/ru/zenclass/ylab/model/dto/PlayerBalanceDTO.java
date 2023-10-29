package ru.zenclass.ylab.model.dto;

import java.math.BigDecimal;

public class PlayerBalanceDTO {
    private String username;
    private BigDecimal balance;


    public PlayerBalanceDTO(String username, BigDecimal balance) {
        this.username = username;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
