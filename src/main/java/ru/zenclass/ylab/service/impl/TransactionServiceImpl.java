package ru.zenclass.ylab.service.impl;


import jakarta.validation.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.zenclass.ylab.aop.annotations.Loggable;
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
import ru.zenclass.ylab.util.AmountValidator;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.TransactionService;
import ru.zenclass.ylab.util.DTOValidator;

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
@Loggable
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final PlayerService playerService;
    private final DTOValidator<AmountDTO> amountValidator;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  PlayerService playerService,
                                  DTOValidator<AmountDTO> amountValidator) {
        this.transactionRepository = transactionRepository;
        this.playerService = playerService;
        this.amountValidator = amountValidator;
    }

    /**
     * Добавляет дебетовую транзакцию для игрока.
     *
     * @param player    Объект игрока, тип {@link Player}.
     * @param amountDTO Объект с информацией о сумме транзакции, тип {@link AmountDTO}.
     * @return Объект с информацией о созданной транзакции, тип {@link TransactionDTO}.
     */
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

    /**
     * Добавляет кредитную транзакцию для игрока.
     *
     * @param player    Объект игрока, тип {@link Player}.
     * @param amountDTO Объект с информацией о сумме транзакции, тип {@link AmountDTO}.
     * @return Объект с информацией о созданной транзакции, тип {@link TransactionDTO}.
     */
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

    /**
     * Просматривает историю транзакций игрока.
     *
     * @param player Объект игрока, тип {@link Player}.
     * @return Объект с информацией об истории транзакций игрока, тип {@link TransactionHistoryDTO}.
     * @throws NoTransactionsFoundException Если не найдено ни одной транзакции для игрока.
     */
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

    /**
     * Приватный метод для валидации суммы транзакции.
     *
     * @param amountDTO Объект с информацией о сумме транзакции, тип {@link AmountDTO}.
     * @throws ValidationException Если сумма транзакции не проходит валидацию.
     */
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

    /**
     * Приватный метод для проверки достаточности средств на счете игрока.
     *
     * @param player Объект игрока, тип {@link Player}.
     * @param amount Сумма транзакции, тип {@link BigDecimal}.
     * @throws NotEnoughMoneyException Если у игрока недостаточно средств для проведения транзакции.
     */
    private void checkSufficientBalance(Player player, BigDecimal amount) {
        if (amount.compareTo(player.getBalance()) > 0) {
            throw new NotEnoughMoneyException();
        }
    }
}









