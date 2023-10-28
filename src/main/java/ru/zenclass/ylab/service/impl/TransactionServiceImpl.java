package ru.zenclass.ylab.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.zenclass.ylab.exception.NotEnoughMoneyException;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.enums.TransactionType;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для обработки транзакций.
 * Этот сервис предоставляет методы для добавления дебетовых и кредитных транзакций,
 * а также для просмотра истории транзакций игрока.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final PlayerService playerService;

    /**
     * Конструктор класса.
     *
     * @param transactionRepository репозиторий транзакций
     * @param playerService сервис для работы с игроками
     */
    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, PlayerService playerService) {
        this.transactionRepository = transactionRepository;
        this.playerService = playerService;
    }

    /**
     * Добавляет дебетовую транзакцию для игрока.
     *
     * @param player игрок, для которого выполняется транзакция
     * @param debitAmount сумма дебетовой транзакции
     * @return объект {@link Transaction}, представляющий добавленную транзакцию
     * @throws NotEnoughMoneyException если на счете игрока недостаточно средств для выполнения транзакции
     */
    public Transaction addDebitTransaction(Player player, BigDecimal debitAmount) {
        if (debitAmount.compareTo(player.getBalance()) <= 0) {
            Transaction transaction = new Transaction();
            transaction.setId(1L);
            transaction.setType(TransactionType.DEBIT);
            transaction.setAmount(debitAmount);
            transaction.setLocalDateTime(LocalDateTime.now());
            Transaction savedTransaction = transactionRepository.addTransaction(transaction, player.getId());
            BigDecimal newBalance = player.getBalance().subtract(debitAmount);
            player.setBalance(newBalance);
            player.setTransaction(transaction);
            playerService.updatePlayer(player);
            return savedTransaction;
        } else {
            throw new NotEnoughMoneyException();
        }
    }

    /**
     * Добавляет кредитную транзакцию для игрока.
     *
     * @param player игрок, для которого выполняется транзакция
     * @param creditAmount сумма кредитной транзакции
     * @return объект {@link Transaction}, представляющий добавленную транзакцию
     */
    public Transaction addCreditTransaction(Player player, BigDecimal creditAmount) {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.CREDIT);
        transaction.setAmount(creditAmount);
        transaction.setLocalDateTime(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.addTransaction(transaction, player.getId());
        BigDecimal newBalance = player.getBalance().add(creditAmount);
        player.setBalance(newBalance);
        player.setTransaction(transaction);
        playerService.updatePlayer(player);
        return savedTransaction;
    }

    /**
     * Получает историю транзакций для определенного игрока.
     *
     * @param id идентификатор игрока
     * @param username имя игрока
     * @return список транзакций {@link Transaction} для данного игрока
     */
    public List<Transaction> viewTransactionHistory(Long id, String username) {
        return transactionRepository.getAllTransactionsByPlayerId(id);
    }
}








