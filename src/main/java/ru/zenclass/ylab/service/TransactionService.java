package ru.zenclass.ylab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.exception.NotEnoughMoneyException;
import ru.zenclass.ylab.exception.TransactionIdIsNotUniqueException;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.model.Transaction;
import ru.zenclass.ylab.model.TransactionType;
import ru.zenclass.ylab.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Сервис для управления транзакциями игроков.
 */
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PlayerService playerService;
    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    /**
     * Конструктор класса TransactionService.
     * @param transactionRepository Репозиторий для хранения транзакций.
     * @param playerService         Сервис для управления данными игроков.
     */
    public TransactionService(TransactionRepository transactionRepository, PlayerService playerService) {
        this.transactionRepository = transactionRepository;
        this.playerService = playerService;
    }

    /**
     * Добавляет дебетовую транзакцию для игрока.
     * @param player        Игрок, для которого создается транзакция.
     * @param scanner       Scanner для ввода суммы дебетовой операции.
     * @param transactionId Уникальный идентификатор транзакции.
     */
    public void addDebitTransaction(Player player, Scanner scanner, String transactionId) {
        System.out.print("Введите сумму дебетовой операции: ");
        if (scanner.hasNextBigDecimal()) {
            BigDecimal debitAmount = scanner.nextBigDecimal();
            scanner.nextLine(); // Очистка буфера после ввода суммы

            try {
                // Проверка, достаточно ли средств на счете игрока для выполнения дебетовой операции
                if (debitAmount.compareTo(player.getBalance()) <= 0) {
                    // Проверка уникальности идентификатора транзакции
                    if (containsTransaction(transactionId)) {
                        // Создаем объект транзакции
                        Transaction transaction = new Transaction();
                        transaction.setId(1L);
                        transaction.setType(TransactionType.DEBIT);
                        transaction.setAmount(debitAmount);
                        transaction.setLocalDateTime(LocalDateTime.now());

                        // Добавляем транзакцию в репозиторий
                        transactionRepository.addTransaction(transaction);

                        // Обновляем баланс игрока
                        BigDecimal newBalance = player.getBalance().subtract(debitAmount);
                        player.setBalance(newBalance);
//                        player.setTransaction(transaction);

                        // Сохраняем обновленные данные игрока в репозитории
                        playerService.updatePlayer(player);

                        // Выводим сообщение о успешной операции
                        System.out.println("------------------------------------------------------------------");
                        System.out.println("Дебетовая операция выполнена успешно.");
                        System.out.println("Баланс на счету игрока составляет: " + player.getBalance());
                        System.out.println("------------------------------------------------------------------");

                        // Производим аудит действий игрока
                        log.info("Пользователь " + player.getUsername() + " успешно совершил дебетовую операцию," +
                                " на сумму: " + debitAmount);
                    } else {
                        throw new TransactionIdIsNotUniqueException("Ошибка: Идентификатор транзакции не уникален.");
                    }
                } else {
                    throw new NotEnoughMoneyException("Недостаточно средств на счете для выполнения дебетовой операции.");
                }
            } catch (NotEnoughMoneyException | TransactionIdIsNotUniqueException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        } else {
            System.out.println("Недопустимый ввод. Пожалуйста, введите число.");
            scanner.nextLine();
        }
    }

    /**
     * Добавляет кредитную транзакцию для игрока.
     * @param player        Игрок, для которого создается транзакция.
     * @param scanner       Scanner для ввода суммы кредитной операции.
     * @param transactionId Уникальный идентификатор транзакции.
     */
    public void addCreditTransaction(Player player, Scanner scanner,String transactionId) {
        System.out.print("Введите сумму кредитной операции: ");
        if (scanner.hasNextBigDecimal()) {
            BigDecimal creditAmount = scanner.nextBigDecimal();
            scanner.nextLine(); // Очистка буфера после ввода суммы

            // Проверка уникальности идентификатора транзакции
            if (containsTransaction(transactionId)) {
                // Создаем объект транзакции
                Transaction transaction = new Transaction();
                transaction.setId(2L);
                transaction.setType(TransactionType.CREDIT);
                transaction.setAmount(creditAmount);
                transaction.setLocalDateTime(LocalDateTime.now());

                // Добавляем транзакцию в репозиторий
                transactionRepository.addTransaction(transaction);

                // Обновляем баланс игрока
                BigDecimal newBalance = player.getBalance().add(creditAmount);
                player.setBalance(newBalance);
//                player.setTransaction(transaction);

                // Сохраняем обновленные данные игрока в репозитории
                playerService.updatePlayer(player);

                System.out.println("------------------------------------------------------------------");
                System.out.println("Операция кредитования выполнена успешно.");
                System.out.println("Баланс на счету игрока составляет: " + player.getBalance());
                System.out.println("------------------------------------------------------------------");

                // Производим аудит действий игрока
                log.info("Пользователь " + player.getUsername() + " успешно совершил дебетовую операцию," +
                        " на сумму: " + creditAmount);
            } else {
                throw new TransactionIdIsNotUniqueException("Ошибка: Идентификатор транзакции не уникален.");
            }
        } else {
            System.out.println("------------------------------------------------------------------");
            System.out.println("Недопустимый ввод. Пожалуйста, введите число.");
            System.out.println("------------------------------------------------------------------");
            scanner.nextLine();
        }
    }

    /**
     * Просматривает историю операций пополнения/снятия средств игрока.
     * @param id Идентификатор игрока, для которого нужно просмотреть историю.
     */
    public void viewTransactionHistory(Long id) {
        Player foundPlayer = playerService.findPlayerById(id);
//        if (foundPlayer.getTransaction() == null) {
//            System.out.println("------------------------------------------------------------------");
//            System.out.println("У игрока: " + foundPlayer.getUsername() + " нету платежной истории");
//            System.out.println("------------------------------------------------------------------");
//        } else {
//            System.out.println("------------------------------------------------------------------");
//            System.out.println("История игрока: " + foundPlayer.getUsername() + " " + foundPlayer.getTransaction());
//            System.out.println("------------------------------------------------------------------");
//
//            // Производим аудит действий игрока
//            log.info("Пользователь " + foundPlayer.getUsername() + " запросил историю по своим операциям");
//        }
    }

    /**
     * Проверяет уникальность идентификатора транзакции.
     * @param transactionId Идентификатор транзакции для проверки.
     * @return true, если идентификатор транзакции уникален, в противном случае false.
     */
    private boolean containsTransaction(String transactionId) {
        // Используем Java Stream API для проверки уникальности идентификатора транзакции
//        List<Transaction> matchingTransactions = transactionRepository.getAllTransactions()
//                .stream()
//                .filter(transaction -> transaction.getId().equals(transactionId)).toList();

        // Если список совпадающих транзакций пуст, идентификатор уникален
        return false;
    }

    /**
     * Показывает текущий баланс игрока.
     * @param id Идентификатор игрока, для которого нужно показать баланс.
     */
    public void showPlayerBalance(Long id){
        Player foundPlayer = playerService.findPlayerById(id);
        System.out.println("------------------------------------------------------------------");
        System.out.println("Баланс на счету игрока составляет: " + foundPlayer.getBalance());
        System.out.println("------------------------------------------------------------------");

        // Производим аудит действий игрока
        log.info("Пользователь " + foundPlayer.getUsername() + " проверил свой баланс");
    }
}








