package ru.zenclass.ylab.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


import java.math.BigDecimal;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDTO {
    private Long id;
    private String username;
    private BigDecimal balance;
}
