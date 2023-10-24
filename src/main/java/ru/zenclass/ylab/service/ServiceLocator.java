package ru.zenclass.ylab.service;

import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.PlayerRepositoryImpl;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.repository.TransactionRepositoryImpl;
import ru.zenclass.ylab.util.JwtUtil;
import ru.zenclass.ylab.validator.RegisterPlayerValidator;

/**
 * Класс-локатор служб для управления зависимостями и предоставления экземпляров сервисов.
 * Этот класс предоставляет статические методы для получения экземпляров различных сервисов
 * и их зависимостей, таких как сервисы для работы с игроками, транзакциями, аутентификацией и запросами.
 */
public class ServiceLocator {
    private static final DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
    private static final TransactionRepository transactionRepository = new TransactionRepositoryImpl(connectionManager);
    private static final PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
    private static final RegisterPlayerValidator registerPlayerValidator = new RegisterPlayerValidator();
    private static final JwtUtil jwtUtil = new JwtUtil();

    private static final PlayerService playerService = new PlayerServiceImpl(playerRepository, registerPlayerValidator, jwtUtil);
    private static final TransactionService transactionService = new TransactionServiceImpl(transactionRepository, playerService);
    private static final AuthService authService = new AuthService(playerService, jwtUtil);
    private static final RequestService requestService = new RequestService();

    /**
     * Получает экземпляр {@link TransactionService} для работы с транзакциями.
     *
     * @return экземпляр {@link TransactionService}
     */
    public static TransactionService getTransactionService() {
        return transactionService;
    }

    /**
     * Получает экземпляр {@link AuthService} для работы с аутентификацией.
     *
     * @return экземпляр {@link AuthService}
     */
    public static AuthService getAuthService() {
        return authService;
    }

    /**
     * Получает экземпляр {@link RequestService} для обработки HTTP-запросов.
     *
     * @return экземпляр {@link RequestService}
     */
    public static RequestService getRequestService() {
        return requestService;
    }

    /**
     * Получает экземпляр {@link PlayerService} для работы с игроками.
     *
     * @return экземпляр {@link PlayerService}
     */
    public static PlayerService getPlayerService() {
        return playerService;
    }
}
