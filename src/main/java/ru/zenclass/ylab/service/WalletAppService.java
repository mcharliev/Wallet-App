package ru.zenclass.ylab.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.exception.NotEnoughMoneyException;
import ru.zenclass.ylab.model.entity.Player;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

/**
 * Сервис для управления взаимодействием с пользовательским интерфейсом и выполнения операций.
 */
@RequiredArgsConstructor
public class WalletAppService {
    private final PlayerService playerService;
    private final TransactionService transactionService;
    private Player loggedInPlayer;
    private Boolean isRunning = true;
    private final Logger log = LoggerFactory.getLogger(WalletAppService.class);


    /**
     * Запускает работу приложения.
     *
     * @param scanner Сканнер для ввода данных пользователя.
     */
    public void run(Scanner scanner) {
        while (isRunning) {
            // Вход в стартовое меню
            if (loggedInPlayer == null) {
                handleLoginMenu(scanner);
            }
            // Вход в меню операций
            else {
                handleOperationsMenu(scanner);
            }
        }
    }

    /**
     * Обрабатывает меню входа пользователя (логин, регистрация, выход).
     *
     * @param scanner Сканнер для ввода данных пользователя.
     */
    private void handleLoginMenu(Scanner scanner) {
        if (loggedInPlayer == null) {
            System.out.println("Меню:");
            System.out.println("1. Вход");
            System.out.println("2. Регистрация");
            System.out.println("3. Выйти из приложения");
            System.out.print("Выберите действие (1-3): ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1 -> {
                        // Вход пользователя
                        System.out.print("Введите логин: ");
                        String username = scanner.nextLine();
                        System.out.print("Введите пароль: ");
                        String password = scanner.nextLine();

                        Optional<Player> optionalPlayer = playerService.login(username, password);
                        if (optionalPlayer.isPresent()) {
                            loggedInPlayer = optionalPlayer.get();
                            System.out.println("Авторизация успешна. Добро пожаловать, " + loggedInPlayer.getUsername() + "!");
                        } else {
                            System.out.println("Ошибка авторизации. Пожалуйста, проверьте введенные данные.");
                        }
                    }
                    case 2 -> {
                        // Регистрация пользователя
                        System.out.print("Введите имя: ");
                        String username = scanner.nextLine();
                        System.out.print("Введите пароль: ");
                        String password = scanner.nextLine();
                        playerService.registerPlayer(username, password);
                    }
                    case 3 -> {
                        // Выход из приложения
                        isRunning = false;
                        if(loggedInPlayer != null) {
                            log.info("Пользователь " + loggedInPlayer.getUsername() + " завершил работу приложения");
                        } else {
                            log.info("Пользователь завершил работу приложения");
                        }
                    }
                    default -> System.out.println("Недопустимый выбор. Попробуйте еще раз.");
                }
            } else {
                System.out.println("Недопустимый ввод. Пожалуйста, введите число.");
                scanner.nextLine(); // Очистка буфера в случае недопустимого ввода
            }
        }
    }

    /**
     * Обрабатывает меню операций пользователя (баланс, дебет, кредит, история, выход).
     *
     * @param scanner Сканнер для ввода данных пользователя.
     */
    private void handleOperationsMenu(Scanner scanner) {
        boolean isOperationsMenuActive = true;
        while (isOperationsMenuActive) {
            System.out.println("Меню:");
            System.out.println("1. Текущий баланс игрока");
            System.out.println("2. Дебетовая операция/снятие средств");
            System.out.println("3. Кредитная операция/пополнение средств");
            System.out.println("4. Просмотр истории операций пополнения/снятия средств");
            System.out.println("5. Выйти из приложения");
            System.out.print("Выберите действие (1-5): ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1 -> {
                        // Опция 1: Текущий баланс игрока
                        transactionService.showPlayerBalance(loggedInPlayer.getId());
                    }
                    case 2 -> {
                        System.out.print("Введите сумму дебетовой операции: ");
                        if (scanner.hasNextBigDecimal()) {
                            BigDecimal debitAmount = scanner.nextBigDecimal();
                            scanner.nextLine();
                            try {
                                transactionService.addDebitTransaction(loggedInPlayer, debitAmount);
                                System.out.println("Дебетовая операция выполнена успешно.");
                                System.out.println("Баланс на счету игрока составляет: " + loggedInPlayer.getBalance());
                            } catch (NotEnoughMoneyException e) {
                                System.out.println("Недостаточно средств на счете для выполнения дебетовой операции.");
                            }
                        } else {
                            System.out.println("Недопустимый ввод. Пожалуйста, введите число.");
                            scanner.nextLine();
                        }
                    }
                    case 3 -> {
                        System.out.print("Введите сумму кредитной операции: ");
                        if (scanner.hasNextBigDecimal()) {
                            BigDecimal creditAmount = scanner.nextBigDecimal();
                            scanner.nextLine();
                            transactionService.addCreditTransaction(loggedInPlayer, creditAmount);
                            System.out.println("Операция кредитования выполнена успешно.");
                            System.out.println("Баланс на счету игрока составляет: " + loggedInPlayer.getBalance());
                        } else {
                            System.out.println("------------------------------------------------------------------");
                            System.out.println("Недопустимый ввод. Пожалуйста, введите число.");
                            System.out.println("------------------------------------------------------------------");
                            scanner.nextLine();
                        }
                    }
                    case 4 ->
                        // Опция 4: Просмотр истории пополнения/снятия средств
                            transactionService.viewTransactionHistory(loggedInPlayer.getId(),loggedInPlayer.getUsername());
                    case 5 -> {
                        // Опция 5: Выйти из приложения
                        isOperationsMenuActive = false;
                        isRunning = false;
                        log.info("Пользователь " + loggedInPlayer.getUsername() + " завершил работу приложения");
                    }
                    default -> System.out.println("Недопустимый выбор. Попробуйте еще раз.");
                }
            } else {
                System.out.println("Недопустимый ввод. Пожалуйста, введите число.");
                scanner.nextLine();
            }
        }
    }
}