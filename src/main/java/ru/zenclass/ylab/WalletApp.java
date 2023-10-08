package ru.zenclass.ylab;

import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.service.AdminService;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.TransactionService;
import ru.zenclass.ylab.service.WalletAppService;

import java.util.Scanner;

public class WalletApp {

    public static void main(String[] args) {
        TransactionRepository transactionRepository = new TransactionRepository();
        PlayerRepository playerRepository = new PlayerRepository();
        PlayerService playerService = new PlayerService(playerRepository);


        WalletAppService walletAppService = new WalletAppService(
                new AdminService(playerService),
                new TransactionService(transactionRepository, playerService)
        );

        try (Scanner scanner = new Scanner(System.in)) {
            walletAppService.run(scanner);
        }
    }
}


