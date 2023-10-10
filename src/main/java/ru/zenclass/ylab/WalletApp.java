package ru.zenclass.ylab;

import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.TransactionService;
import ru.zenclass.ylab.service.WalletAppService;

import java.util.Scanner;

/**
 * Главный класс приложения для управления операциями с кошельком пользователей.
 * Создает необходимые зависимости и запускает работу приложения через {@link WalletAppService}.
 */
public class WalletApp {

    /**
     * Точка входа в приложение. Создает необходимые сервисы и запускает работу приложения.
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        // Создание репозиториев и сервисов
        TransactionRepository transactionRepository = new TransactionRepository();
        PlayerRepository playerRepository = new PlayerRepository();
        PlayerService playerService = new PlayerService(playerRepository);

        // Создание и инициализация WalletAppService с созданными сервисами
        WalletAppService walletAppService = new WalletAppService(
                new PlayerService(playerRepository),
                new TransactionService(transactionRepository, playerService)
        );

        try (Scanner scanner = new Scanner(System.in)) {
            // Запуск работы приложения
            walletAppService.run(scanner);
        }
    }
}


