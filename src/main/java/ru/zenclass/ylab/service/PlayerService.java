package ru.zenclass.ylab.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.exception.AuthorisationException;
import ru.zenclass.ylab.exception.PlayerAlreadyExistException;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.repository.PlayerRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

/**
 * Сервис для управления данными игроков.
 */
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final Logger log = LoggerFactory.getLogger(PlayerService.class);
    /**
     * Конструктор класса PlayerService.
     * @param playerRepository Репозиторий для хранения данных игроков.
     */

    /**
     * Добавляет игрока в репозиторий.
     *
     * @param player Игрок для добавления.
     */
    public void addPlayer(Player player) {
        playerRepository.addPlayer(player);
    }

    /**
     * Возвращает список всех игроков.
     *
     * @return Список всех игроков.
     */
    public List<Player> getAllPlayers() {
        return playerRepository.getAllPlayers();
    }

    /**
     * Находит игрока по его идентификатору.
     *
     * @param id Идентификатор игрока для поиска.
     * @return Найденный игрок или null, если игрок не найден.
     */
    public Player findPlayerById(Long id) {
        return playerRepository.findPlayerById(id);
    }

    /**
     * Обновляет данные игрока.
     *
     * @param updatedPlayer Обновленные данные игрока.
     */
    public void updatePlayer(Player updatedPlayer) {
        List<Player> players = getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player existingPlayer = players.get(i);

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

        try {
            Optional<Player> optPlayer = playerRepository.findPlayerByUsername(username);
            // Проверяем, существует ли уже игрок с таким именем
            if (optPlayer.isEmpty()) {
                Player player = new Player();
                player.setUsername(username);
                player.setPassword(password);
                player.setBalance(BigDecimal.ZERO);

                playerRepository.addPlayer(player);

                log.info("Пользователь " + player.getUsername() + " успешно зарегистрировался");
                System.out.println("------------------------------------------------------------------");
                System.out.println("Регистрация успешна. Теперь вы можете войти.");
                System.out.println("------------------------------------------------------------------");

            } else {
                throw new PlayerAlreadyExistException();
            }
        } catch (PlayerAlreadyExistException e) {
            System.out.println("Пользователь с таким именем уже существует. Пожалуйста, " +
                    "выберите другое имя.");
        }
    }

    /**
     * Авторизует игрока.
     *
     * @param loggedInPlayer Игрок, который вошел в систему.
     * @return Игрок, который вошел в систему после авторизации.
     */
    public Player login(Player loggedInPlayer, Scanner scanner) {

        System.out.print("Введите логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        try {
            Player player = authenticatePlayer(username, password);

            if (player != null) {
                loggedInPlayer = player;
                System.out.println("Авторизация успешна. Добро пожаловать, " + loggedInPlayer.getUsername() + "!");
                log.info("Пользователь " + loggedInPlayer.getUsername() + " прошел авторизацию");
            } else {
                throw new AuthorisationException();
            }
        } catch (AuthorisationException e) {
            log.error("Ошибка авторизации пользователя");
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
        Optional<Player> optPlayer = playerRepository.findPlayerByUsername(username);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            if (player.getUsername().equals(username) && player.getPassword().equals(password)) {
                return player;
            }
        }
        return null;
    }
}
