package ru.zenclass.ylab.service;

import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WalletService {
    private static List<Player> players = new ArrayList<>();
    private static List<Transaction> transactions = new ArrayList<>();


    public static void main(String[] args) {
        System.out.println(players);
        WalletService walletService = new WalletService();
        walletService.registerPlayer();
    }



    private Player savePlayer(String username, String password) {
        Player newPlayer = new Player();
        newPlayer.setUsername(username);
        newPlayer.setPassword(password);
        return newPlayer;
    }

}
