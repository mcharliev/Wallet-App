package ru.zenclass.ylab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.exception.PlayerAlreadyExistException;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.repository.PlayerRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Сервис для управления данными игроков.
 */
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final Logger log = LoggerFactory.getLogger(PlayerService.class);
    /**
     * Конструктор класса PlayerService.
     * @param playerRepository Репозиторий для хранения данных игроков.
     */
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Добавляет игрока в репозиторий.
     * @param player Игрок для добавления.
     */
    public void addPlayer(Player player) {
        playerRepository.addPlayer(player);
    }

    /**
     * Возвращает список всех игроков.
     * @return Список всех игроков.
     */
    public List<Player> getAllPlayers() {
        return playerRepository.getAllPlayers();
    }

    /**
     * Находит игрока по его идентификатору.
     * @param id Идентификатор игрока для поиска.
     * @return Найденный игрок или null, если игрок не найден.
     */
    public Player findPlayerById(String id) {
        return playerRepository.findPlayerById(id);
    }

    /**
     * Обновляет данные игрока.
     * @param updatedPlayer Обновленные данные игрока.
     */
    public void updatePlayer(Player updatedPlayer) {
        List<Player> players = getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player existingPlayer = players.get(i);
            if (existingPlayer.getId().equals(updatedPlayer.getId())) {
                players.set(i, updatedPlayer);
            }
        }
    }
    /**
     * Регистрирует нового игрока.
     */
    public void registerPlayer(Scanner scanner) {
        System.out.print("Введите имя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        // Проверяем, существует ли уже игрок с таким именем
        if (!isPlayerExists(username)) {
            Player player = new Player();
            player.setUsername(username);
            player.setPassword(password);
            player.setId(4L);
            player.setBalance(BigDecimal.valueOf(0));
            playerRepository.addPlayer(player);

            log.info("Пользователь " + player.getUsername() + " успешно зарегистрировался");
            System.out.println("------------------------------------------------------------------");
            System.out.println("Регистрация успешна. Теперь вы можете войти.");
            System.out.println("------------------------------------------------------------------");

        } else {
            throw new PlayerAlreadyExistException("Пользователь с таким именем уже существует. Пожалуйста, " +
                    "выберите другое имя.");
        }
    }

    /**
     * Авторизует игрока.
     *
     * @param loggedInPlayer Игрок, который вошел в систему.
     * @return Игрок, который вошел в систему после авторизации.
     */
    public Player login(Player loggedInPlayer,Scanner scanner) {
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

    /**
     * Аутентифицирует игрока по его имени и паролю.
     *
     * @param username Логин (имя) игрока.
     * @param password Пароль игрока.
     * @return Игрок, если аутентификация прошла успешно, иначе null.
     */
    private Player authenticatePlayer(String username, String password) {
        List<Player> players = playerRepository.getAllPlayers();
        for (Player player : players) {
            if (player.getUsername().equals(username) && player.getPassword().equals(password)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Проверяет существование игрока с заданным именем.
     *
     * @param username Логин (имя) игрока для проверки.
     * @return true, если игрок с таким именем существует, иначе false.
     */
    private boolean isPlayerExists(String username) {
        List<Player> players = playerRepository.getAllPlayers();
        for (Player player : players) {
            if (player.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
