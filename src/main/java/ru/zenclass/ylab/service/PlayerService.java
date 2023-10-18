package ru.zenclass.ylab.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.repository.PlayerRepository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Сервис для управления данными игрока.
 * Этот сервис предоставляет методы для выполнения основных операций, таких как поиск, обновление, регистрация и вход.
 */
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    /**
     * Поиск игрока по его идентификатору.
     *
     * @param id идентификатор игрока.
     * @return  {@link Player}.
     * @throws PlayerNotFoundException если игрок с данным идентификатором не найден.
     */
    public Player findPlayerById(Long id) {
        return playerRepository.findPlayerById(id).orElseThrow(PlayerNotFoundException::new);
    }

    /**
     * Обновление данных игрока.
     *
     * @param updatedPlayer объект игрока с обновленными данными.
     */
    public void updatePlayer(Player updatedPlayer) {
        playerRepository.updatePlayer(updatedPlayer);
    }

    /**
     * Регистрация нового игрока.
     *
     */
    public Optional<Player> registerPlayer(String username, String password) {
        Optional<Player> optPlayer = playerRepository.findPlayerByUsername(username);
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
            return Optional.of(player);
        } else {
            log.error("Ошибка регистрации пользователя");
            System.out.println("Пользователь с таким именем уже существует. Пожалуйста, выберите другое имя.");
            return Optional.empty();
        }
    }

    public Optional<Player> login(String username, String password) {
        Optional<Player> playerOpt = authenticatePlayer(username, password);
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            log.info("Пользователь " + player.getUsername() + " прошел авторизацию");
            return playerOpt;
        } else {
            log.error("Ошибка авторизации пользователя с именем " + username);
            return Optional.empty();
        }
    }

    /**
     * Аутентификация игрока на основе имени пользователя и пароля.
     *
     * @param username имя пользователя.
     * @param password пароль пользователя.
     * @return {@link Player}, если аутентификация прошла успешно, иначе null.
     */
    private Optional<Player> authenticatePlayer(String username, String password) {
        // Поиск игрока в репозитории по имени пользователя.
        Optional<Player> optPlayer = playerRepository.findPlayerByUsername(username);

        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();

            // Проверка соответствия имени пользователя и пароля.
            if (player.getUsername().equals(username) && player.getPassword().equals(password)) {
                return optPlayer; // Возвращаем игрока, если данные совпадают.
            }
        }
        return Optional.empty();
    }
}
