package ru.zenclass.ylab.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.exception.NotEnoughMoneyException;
import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.model.Transaction;
import ru.zenclass.ylab.model.TransactionType;
import ru.zenclass.ylab.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для обработки транзакций.
 */
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    // Репозиторий транзакций для работы с данными о транзакциях.
    private final TransactionRepository transactionRepository;

    // Сервис для работы с данными о игроках.
    private final PlayerService playerService;

    // Логгер для аудита и мониторинга действий в сервисе.
    private final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    /**
     * Метод для добавления дебетовой транзакции.
     *
     * @param player  Игрок, который хочет выполнить дебетовую операцию.
     */
    public void addDebitTransaction(Player player, BigDecimal debitAmount) {

        if (debitAmount.compareTo(player.getBalance()) <= 0) {
            Transaction transaction = new Transaction();
            transaction.setId(1L);
            transaction.setType(TransactionType.DEBIT);
            transaction.setAmount(debitAmount);
            transaction.setLocalDateTime(LocalDateTime.now());

            transactionRepository.addTransaction(transaction, player.getId());

            BigDecimal newBalance = player.getBalance().subtract(debitAmount);
            player.setBalance(newBalance);
            player.setTransaction(transaction);

            playerService.updatePlayer(player);

            log.info("Пользователь " + player.getUsername() + " успешно совершил дебетовую операцию," +
                    " на сумму: " + debitAmount);
        } else {
            log.info("Ошибка дебетовой операции, недосточно средств на счету игрока ");
            throw new NotEnoughMoneyException();
        }
    }

    /**
     * Метод для добавления кредитной транзакции.
     *
     * @param player  Игрок, который хочет выполнить кредитную операцию.
     */
    public void addCreditTransaction(Player player, BigDecimal creditAmount) {
        // Создаем новую кредитную транзакцию.
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.CREDIT);
        transaction.setAmount(creditAmount);
        transaction.setLocalDateTime(LocalDateTime.now());

        // Добавляем транзакцию в репозиторий.
        transactionRepository.addTransaction(transaction, player.getId());

        // Обновляем баланс игрока после кредитования.
        BigDecimal newBalance = player.getBalance().add(creditAmount);
        player.setBalance(newBalance);
        player.setTransaction(transaction);

        // Обновляем информацию о игроке в базе данных.
        playerService.updatePlayer(player);

        log.info("Пользователь " + player.getUsername() + " успешно совершил кредитную операцию," +
                " на сумму: " + creditAmount);
    }

    /**
     * Метод для просмотра истории транзакций игрока.
     *
     * @param id       ID игрока.
     * @param username Имя пользователя игрока.
     */
    public void viewTransactionHistory(Long id, String username) {
        List<Transaction> allTransactionsByPlayerId = transactionRepository.getAllTransactionsByPlayerId(id);

        if (allTransactionsByPlayerId.isEmpty()) {
            System.out.println("------------------------------------------------------------------");
            System.out.println("У игрока: " + username + " нету платежной истории");
            System.out.println("------------------------------------------------------------------");
        } else {
            System.out.println("------------------------------------------------------------------");
            System.out.println("История игрока: " + username + " " + allTransactionsByPlayerId);
            System.out.println("------------------------------------------------------------------");
            log.info("Пользователь " + username + " запросил историю по своим операциям");
        }
    }

    /**
     * Метод для отображения баланса игрока.
     *
     * @param id ID игрока.
     */
    public void showPlayerBalance(Long id) {
        try {
            Player foundPlayer = playerService.findPlayerById(id);
            System.out.println("------------------------------------------------------------------");
            System.out.println("Баланс на счету игрока составляет: " + foundPlayer.getBalance());
            System.out.println("------------------------------------------------------------------");
            log.info("Пользователь " + foundPlayer.getUsername() + " проверил свой баланс");
        } catch (PlayerNotFoundException e) {
            log.error("Игрок с ID " + id + " не найден.");
        }
    }
}







