package ru.zenclass.ylab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.exception.PlayerAlreadyExistException;
import ru.zenclass.ylab.model.Player;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class AdminService {

    private final PlayerService playerService;
    private final Scanner scanner = new Scanner(System.in);
    private final Logger log = LoggerFactory.getLogger(AdminService.class);

    public AdminService(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Метод для регистрации игрока
    public void registerPlayer() {
        System.out.print("Введите имя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        // Проверяем, существует ли уже игрок с таким именем
        if (!isPlayerExists(username)) {
            Player player = new Player();
            player.setUsername(username);
            player.setPassword(password);
            player.setId(UUID.randomUUID().toString());
            player.setBalance(BigDecimal.valueOf(0));
            playerService.addPlayer(player);

            log.info("Пользователь " + player.getUsername() + " успешно зарегистрировался");
            System.out.println("------------------------------------------------------------------");
            System.out.println("Регистрация успешна. Теперь вы можете войти.");
            System.out.println("------------------------------------------------------------------");

        } else {
            throw new PlayerAlreadyExistException("Пользователь с таким именем уже существует. Пожалуйста, " +
                    "выберите другое имя.");
        }
    }

    // Метод для авторизации игрока
    public Player login(Player loggedInPlayer) {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        Player player = authenticatePlayer(username, password);

        if (player != null) {
            loggedInPlayer = player;
            System.out.println("Авторизация успешна. Добро пожаловать, " + loggedInPlayer.getUsername() + "!");
            log.info("Пользователь " + loggedInPlayer.getUsername() + " прошел авторизацию");
        } else {
            System.out.println("Ошибка авторизации. Пожалуйста, проверьте введенные данные.");
        }
        return loggedInPlayer;
    }

    // Метод для аутентификации игрока
    private Player authenticatePlayer(String username, String password) {
        List<Player> players = playerService.getAllPlayers();
        for (Player player : players) {
            if (player.getUsername().equals(username) && player.getPassword().equals(password)) {
                return player;
            }
        }
        return null;
    }

    // Метод для проверки существования игрока с заданным именем
    private boolean isPlayerExists(String username) {
        List<Player> players = playerService.getAllPlayers();
        for (Player player : players) {
            if (player.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
