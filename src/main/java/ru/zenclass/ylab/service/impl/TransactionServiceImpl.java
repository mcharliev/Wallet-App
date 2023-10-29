package ru.zenclass.ylab.service.impl;


import jakarta.validation.ConstraintViolation;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.zenclass.ylab.exception.NoTransactionsFoundException;
import ru.zenclass.ylab.exception.NotEnoughMoneyException;
import ru.zenclass.ylab.exception.ValidationException;
import ru.zenclass.ylab.model.dto.AmountDTO;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.dto.TransactionHistoryDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.enums.TransactionType;
import ru.zenclass.ylab.model.mapper.TransactionMapper;
import ru.zenclass.ylab.model.util.AmountValidator;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Сервис для обработки транзакций.
 * Этот сервис предоставляет методы для добавления дебетовых и кредитных транзакций,
 * а также для просмотра истории транзакций игрока.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final PlayerService playerService;
    private final AmountValidator amountValidator;


    /**
     * Конструктор класса.
     *
     * @param transactionRepository репозиторий транзакций
     * @param playerService         сервис для работы с игроками
     */
    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  PlayerService playerService,
                                  AmountValidator amountValidator) {
        this.transactionRepository = transactionRepository;
        this.playerService = playerService;
        this.amountValidator = amountValidator;
    }

    public TransactionDTO addDebitTransaction(Player player, AmountDTO amountDTO) {
        validateAmount(amountDTO);
        checkSufficientBalance(player, amountDTO.getAmount());
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setType(TransactionType.DEBIT);
        transaction.setAmount(amountDTO.getAmount());
        transaction.setLocalDateTime(LocalDateTime.now());
        transactionRepository.addTransaction(transaction, player.getId());
        BigDecimal newBalance = player.getBalance().subtract(amountDTO.getAmount());
        player.setBalance(newBalance);
        player.setTransaction(transaction);
        playerService.updatePlayer(player);
        return TransactionMapper.INSTANCE.toDTO(transaction);
    }

    public TransactionDTO addCreditTransaction(Player player, AmountDTO amountDTO) {
        validateAmount(amountDTO);
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.CREDIT);
        transaction.setAmount(amountDTO.getAmount());
        transaction.setLocalDateTime(LocalDateTime.now());
        transactionRepository.addTransaction(transaction, player.getId());
        BigDecimal newBalance = player.getBalance().add(amountDTO.getAmount());
        player.setBalance(newBalance);
        player.setTransaction(transaction);
        playerService.updatePlayer(player);
        return TransactionMapper.INSTANCE.toDTO(transaction);
    }

    public TransactionHistoryDTO viewTransactionHistory(Player player) {
        List<Transaction> allTransactionsByPlayerId
                = transactionRepository.getAllTransactionsByPlayerId(player.getId());
        if (allTransactionsByPlayerId.isEmpty()) {
            throw new NoTransactionsFoundException();
        }
        List<TransactionDTO> transactionDTOList = allTransactionsByPlayerId.stream()
                .map(TransactionMapper.INSTANCE::toDTO)
                .toList();
        TransactionHistoryDTO transactionHistoryDTO = new TransactionHistoryDTO();
        transactionHistoryDTO.setMessage("История транзакций игрока " + player.getUsername());
        transactionHistoryDTO.setTransactionDTOList(transactionDTOList);
        return transactionHistoryDTO;
    }

    private void validateAmount(AmountDTO amountDTO) {
        Set<ConstraintViolation<AmountDTO>> violations = amountValidator.validate(amountDTO);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<AmountDTO> violation : violations) {
                sb.append(violation.getMessage()).append(". ");
            }
            throw new ValidationException(sb.toString(), violations);
        }
    }

    private void checkSufficientBalance(Player player, BigDecimal amount) {
        if (amount.compareTo(player.getBalance()) > 0) {
            throw new NotEnoughMoneyException();
        }
    }
}










