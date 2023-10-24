package ru.zenclass.ylab.service;

import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.PlayerRepositoryImpl;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.repository.TransactionRepositoryImpl;

public class ServiceLocator {
    private static final DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
    private static final TransactionRepository transactionRepository = new TransactionRepositoryImpl(connectionManager);
    private static final PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
    private static final PlayerService playerService = new PlayerServiceImpl(playerRepository);
    private static final TransactionService transactionService = new TransactionServiceImpl(transactionRepository, playerService);
    private static final AuthService authService = new AuthService(playerService);
    private static final RequestService requestService = new RequestService();

    public static TransactionService getTransactionService() {
        return transactionService;
    }

    public static AuthService getAuthService() {
        return authService;
    }

    public static RequestService getRequestService() {
        return requestService;
    }


    public static PlayerService getPlayerService() {
        return playerService;
    }
}
