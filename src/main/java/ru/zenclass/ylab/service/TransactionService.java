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
import java.util.Scanner;

/**
 * Сервис для обработки транзакций.
 */
@RequiredArgsConstructor
public class TransactionService {

    // Репозиторий транзакций для работы с данными о транзакциях.
    private final TransactionRepository transactionRepository;

    // Сервис для работы с данными о игроках.
    private final PlayerService playerService;

    // Логгер для аудита и мониторинга действий в сервисе.
    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    /**
     * Метод для добавления дебетовой транзакции.
     *
     * @param player  Игрок, который хочет выполнить дебетовую операцию.
     * @param scanner Сканер для ввода данных от пользователя.
     */
    public void addDebitTransaction(Player player, Scanner scanner) {
        // Считываем сумму дебетовой операции.
        System.out.print("Введите сумму дебетовой операции: ");

        // Проверяем, что пользователь ввел число.
        if (scanner.hasNextBigDecimal()) {
            BigDecimal debitAmount = scanner.nextBigDecimal();
            scanner.nextLine(); // Очищаем буфер от лишних символов.

            try {
                // Проверяем, что на счету игрока достаточно средств для дебетовой операции.
                if (debitAmount.compareTo(player.getBalance()) <= 0) {
                    // Создаем новую дебетовую транзакцию.
                    Transaction transaction = new Transaction();
                    transaction.setId(1L);
                    transaction.setType(TransactionType.DEBIT);
                    transaction.setAmount(debitAmount);
                    transaction.setLocalDateTime(LocalDateTime.now());

                    // Добавляем транзакцию в репозиторий.
                    transactionRepository.addTransaction(transaction, player.getId());

                    // Обновляем баланс игрока после дебетовой операции.
                    BigDecimal newBalance = player.getBalance().subtract(debitAmount);
                    player.setBalance(newBalance);
                    player.setTransaction(transaction);

                    // Обновляем информацию о игроке в базе данных.
                    playerService.updatePlayer(player);

                    System.out.println("------------------------------------------------------------------");
                    System.out.println("Дебетовая операция выполнена успешно.");
                    System.out.println("Баланс на счету игрока составляет: " + player.getBalance());
                    System.out.println("------------------------------------------------------------------");

                    log.info("Пользователь " + player.getUsername() + " успешно совершил дебетовую операцию," +
                            " на сумму: " + debitAmount);
                } else {
                    // Генерируем исключение, если на счету недостаточно средств.
                    throw new NotEnoughMoneyException();
                }
                // Сразу ловлю исключение и вывожу сообщение об ошибке, чтобы приложение не упало
            } catch (NotEnoughMoneyException e) {
                System.out.println("Недостаточно средств на счете для выполнения дебетовой операции.");
            }
        } else {
            System.out.println("Недопустимый ввод. Пожалуйста, введите число.");
            scanner.nextLine();
        }
    }

    /**
     * Метод для добавления кредитной транзакции.
     *
     * @param player  Игрок, который хочет выполнить кредитную операцию.
     * @param scanner Сканер для ввода данных от пользователя.
     */
    public void addCreditTransaction(Player player, Scanner scanner) {
        // Считываем сумму кредитной операции.
        System.out.print("Введите сумму кредитной операции: ");

        if (scanner.hasNextBigDecimal()) {
            BigDecimal creditAmount = scanner.nextBigDecimal();
            scanner.nextLine();

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

            System.out.println("------------------------------------------------------------------");
            System.out.println("Операция кредитования выполнена успешно.");
            System.out.println("Баланс на счету игрока составляет: " + player.getBalance());
            System.out.println("------------------------------------------------------------------");

            log.info("Пользователь " + player.getUsername() + " успешно совершил кредитную операцию," +
                    " на сумму: " + creditAmount);

        } else {
            System.out.println("------------------------------------------------------------------");
            System.out.println("Недопустимый ввод. Пожалуйста, введите число.");
            System.out.println("------------------------------------------------------------------");
            scanner.nextLine();
        }
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








