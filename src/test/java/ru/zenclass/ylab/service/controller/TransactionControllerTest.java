package ru.zenclass.ylab.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.zenclass.ylab.configuration.AppConfig;
import ru.zenclass.ylab.configuration.WebConfig;
import ru.zenclass.ylab.controller.TransactionController;
import ru.zenclass.ylab.controller.advice.ExceptionHandlerAdvice;
import ru.zenclass.ylab.exception.AuthenticationException;
import ru.zenclass.ylab.exception.NoTransactionsFoundException;
import ru.zenclass.ylab.exception.NotEnoughMoneyException;
import ru.zenclass.ylab.model.dto.AmountDTO;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.dto.TransactionHistoryDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.TransactionService;
import ru.zenclass.ylab.util.JwtInterceptor;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private JwtInterceptor jwtInterceptor;

    private Player authenticatedPlayer;
    private AmountDTO amountDTO;
    private TransactionDTO transactionDTO;

    @BeforeEach
    public void setUp() {
        authenticatedPlayer = new Player("user", "pass");
        amountDTO = new AmountDTO();
        amountDTO.setAmount(new BigDecimal(100));
        transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(amountDTO.getAmount());

        Mockito.when(jwtInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Object.class)))
                .thenAnswer(invocation -> {
                    HttpServletRequest request = invocation.getArgument(0);
                    request.setAttribute("authenticatedPlayer", authenticatedPlayer);
                    return true;
                });

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TransactionController(transactionService))
                .addInterceptors(jwtInterceptor)
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .build();
    }

    @Test
    void addCreditTransaction() throws Exception {
        given(transactionService.addCreditTransaction(any(Player.class), any(AmountDTO.class))).willReturn(transactionDTO);

        mockMvc.perform(post("/transactions/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amountDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(transactionDTO.getAmount()));
    }

    @Test
    void addDebitTransaction() throws Exception {
        amountDTO.setAmount(new BigDecimal(50));
        transactionDTO.setAmount(amountDTO.getAmount());

        given(transactionService.addDebitTransaction(any(Player.class), any(AmountDTO.class))).willReturn(transactionDTO);

        mockMvc.perform(post("/transactions/debit")
                        .requestAttr("authenticatedPlayer", authenticatedPlayer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amountDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(transactionDTO.getAmount()));
    }

    @Test
    void viewTransactionHistory() throws Exception {
        TransactionHistoryDTO transactionHistoryDTO = new TransactionHistoryDTO();
        transactionHistoryDTO.setMessage("История транзакций игрока user");

        given(transactionService.viewTransactionHistory(authenticatedPlayer)).willReturn(transactionHistoryDTO);

        mockMvc.perform(get("/transactions/history")
                        .requestAttr("authenticatedPlayer", authenticatedPlayer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(transactionHistoryDTO.getMessage()));
    }

    @Test
    void addDebitTransaction_NotEnoughMoney() throws Exception {
        amountDTO.setAmount(new BigDecimal(150)); // Override for this specific test

        given(transactionService.addDebitTransaction(any(Player.class), any(AmountDTO.class)))
                .willThrow(new NotEnoughMoneyException());

        mockMvc.perform(post("/transactions/debit")
                        .requestAttr("authenticatedPlayer", authenticatedPlayer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amountDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ошибка дебетовой транзакции, недостаточно денег на счете"));
    }

    @Test
    void viewTransactionHistory_NoTransactionsFound() throws Exception {
        given(transactionService.viewTransactionHistory(authenticatedPlayer))
                .willThrow(new NoTransactionsFoundException());

        mockMvc.perform(get("/transactions/history")
                        .requestAttr("authenticatedPlayer", authenticatedPlayer))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("У игрока еще нету истории транзакций"));
    }

    @Test
    void addCreditTransaction_AuthenticationError() throws Exception {
        given(transactionService.addCreditTransaction(any(Player.class), any(AmountDTO.class)))
                .willThrow(new AuthenticationException());

        mockMvc.perform(post("/transactions/credit")
                        .requestAttr("authenticatedPlayer", authenticatedPlayer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amountDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Ошибка авторизации, пожалуйста проверьте введенные данные"));
    }
}
