package ru.zenclass.ylab.service.service;


import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zenclass.ylab.exception.NoTransactionsFoundException;
import ru.zenclass.ylab.exception.NotEnoughMoneyException;
import ru.zenclass.ylab.exception.ValidationException;
import ru.zenclass.ylab.model.dto.AmountDTO;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.dto.TransactionHistoryDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.enums.TransactionType;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.impl.TransactionServiceImpl;
import ru.zenclass.ylab.util.DTOValidator;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private DTOValidator<AmountDTO> amountValidator;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Player player;
    private AmountDTO amountDTO;
    private Transaction debitTransaction;
    private Transaction creditTransaction;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(1L);
        player.setBalance(new BigDecimal("100.00"));

        amountDTO = new AmountDTO();
        amountDTO.setAmount(new BigDecimal("50.00"));

        debitTransaction = new Transaction();
        debitTransaction.setId(1L);
        debitTransaction.setType(TransactionType.DEBIT);
        debitTransaction.setAmount(amountDTO.getAmount());

        creditTransaction = new Transaction();
        creditTransaction.setId(2L);
        creditTransaction.setType(TransactionType.CREDIT);
        creditTransaction.setAmount(amountDTO.getAmount());
    }

    @Test
    void addDebitTransaction_Success() {
        when(amountValidator.validate(amountDTO)).thenReturn(Collections.emptySet());
        doNothing().when(transactionRepository).addTransaction(any(Transaction.class), eq(player.getId()));
        doNothing().when(playerService).updatePlayer(player);

        TransactionDTO result = transactionService.addDebitTransaction(player, amountDTO);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(TransactionType.DEBIT);
        assertThat(result.getAmount()).isEqualByComparingTo(amountDTO.getAmount());
        verify(transactionRepository).addTransaction(any(Transaction.class), eq(player.getId()));
        verify(playerService).updatePlayer(player);
    }

    @Test
    void addDebitTransaction_InsufficientBalance() {
        amountDTO.setAmount(new BigDecimal("150.00"));

        assertThatThrownBy(() -> transactionService.addDebitTransaction(player, amountDTO))
                .isInstanceOf(NotEnoughMoneyException.class);

        verify(transactionRepository, never()).addTransaction(any(Transaction.class), eq(player.getId()));
        verify(playerService, never()).updatePlayer(player);
    }

    @Test
    void addCreditTransaction_Success() {
        when(amountValidator.validate(amountDTO)).thenReturn(Collections.emptySet());
        doNothing().when(transactionRepository).addTransaction(any(Transaction.class), eq(player.getId()));
        doNothing().when(playerService).updatePlayer(player);

        TransactionDTO result = transactionService.addCreditTransaction(player, amountDTO);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(TransactionType.CREDIT);
        assertThat(result.getAmount()).isEqualByComparingTo(amountDTO.getAmount());
        verify(transactionRepository).addTransaction(any(Transaction.class), eq(player.getId()));
        verify(playerService).updatePlayer(player);
    }

    @Test
    void viewTransactionHistory_Success() {
        List<Transaction> transactions = List.of(debitTransaction, creditTransaction);
        when(transactionRepository.getAllTransactionsByPlayerId(player.getId())).thenReturn(transactions);

        TransactionHistoryDTO result = transactionService.viewTransactionHistory(player);

        assertThat(result).isNotNull();
        assertThat(result.getTransactionDTOList()).hasSize(2);
        assertThat(result.getMessage()).isEqualTo("История транзакций игрока " + player.getUsername());
    }

    @Test
    void viewTransactionHistory_NoTransactionsFound() {
        when(transactionRepository.getAllTransactionsByPlayerId(player.getId())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> transactionService.viewTransactionHistory(player))
                .isInstanceOf(NoTransactionsFoundException.class);
    }

    @Test
    void validateAmount_InvalidAmount() {
        Set<ConstraintViolation<AmountDTO>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));

        when(amountValidator.validate(amountDTO)).thenReturn(violations);

        assertThatThrownBy(() -> transactionService.addDebitTransaction(player, amountDTO))
                .isInstanceOf(ValidationException.class);

        verify(transactionRepository, never()).addTransaction(any(Transaction.class), eq(player.getId()));
        verify(playerService, never()).updatePlayer(player);
    }
}