package ru.zenclass.ylab.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



import java.math.BigDecimal;



@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDTO {
    private Long id;
    private String username;
    private BigDecimal balance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
