package ru.zenclass.ylab;

import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.liquibase.LiquibaseMigrationRunner;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.PlayerRepositoryImpl;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.repository.TransactionRepositoryImpl;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.TransactionService;
import ru.zenclass.ylab.service.WalletAppService;

import java.util.Scanner;

/**
 * Главный класс приложения, обеспечивающий работу с кошельком пользователей.
 * Инициализирует и управляет потоком выполнения операций.
 */
public class WalletApp {

    /**
     * Главный метод приложения. Инициализирует репозитории, сервисы и управляет выполнением операций.
     *
     * @param args Аргументы командной строки (в текущей реализации не используются).
     */
    public static void main(String[] args) {
        // Инициализация менеджера подключения к базе данных и выполнение миграций
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        LiquibaseMigrationRunner migrationRunner = new LiquibaseMigrationRunner(connectionManager);
        migrationRunner.runMigrations();

        // Инициализация репозиториев и сервисов для управления данными игроков и транзакций
        PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
        TransactionRepository transactionRepository = new TransactionRepositoryImpl(connectionManager);
        PlayerService playerService = new PlayerService(playerRepository);

        // Создание основного сервиса приложения и передача ему инициализированных сервисов
        WalletAppService walletAppService = new WalletAppService(playerService,
                new TransactionService(transactionRepository, playerService));

        // Инициализация ввода данных и запуск основного сервиса приложения
        try (Scanner scanner = new Scanner(System.in)) {
            walletAppService.run(scanner);
        }
    }
}


