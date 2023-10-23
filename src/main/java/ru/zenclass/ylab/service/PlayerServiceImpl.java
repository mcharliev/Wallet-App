package ru.zenclass.ylab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.aop.annotations.Loggable;
import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.repository.PlayerRepository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Сервис для управления данными игрока.
 * Этот сервис предоставляет методы для выполнения основных операций, таких как поиск, обновление, регистрация и вход.
 */

@Loggable
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Поиск игрока по его идентификатору.
     *
     * @param id идентификатор игрока.
     * @return {@link Player}.
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
     */
    @Loggable
    public Optional<Player> registerPlayer(String username, String password) {
        Optional<Player> optPlayer = playerRepository.findPlayerByUsername(username);
        if (optPlayer.isEmpty()) {
            Player player = new Player();
            player.setUsername(username);
            player.setPassword(password);
            player.setBalance(BigDecimal.ZERO);
            playerRepository.addPlayer(player);
            log.info("Пользователь " + player.getUsername() + " успешно зарегистрировался");
            return Optional.of(player);
        } else {
            log.error("Ошибка регистрации пользователя. Пользователь с таким именем уже существует.");
            return Optional.empty();
        }
    }

    public Optional<Player> login(String username, String password) {
        Optional<Player> playerOpt = authenticatePlayer(username, password);

        if (playerOpt.isPresent()) {
            log.info("Пользователь " + username + " прошел авторизацию");
        } else {
            log.error("Ошибка авторизации пользователя с именем " + username);
        }

        return playerOpt;
    }

    @Override
    public Optional<Player> findPlayerByUsername(String username) {
        return playerRepository.findPlayerByUsername(username);
    }

    /**
     * Аутентификация игрока на основе имени пользователя и пароля.
     *
     * @param username имя пользователя.
     * @param password пароль пользователя.
     * @return {@link Player}, если аутентификация прошла успешно, иначе null.
     */
    private Optional<Player> authenticatePlayer(String username, String password) {
        Optional<Player> optPlayer = playerRepository.findPlayerByUsername(username);
        return optPlayer.filter(player -> player.getPassword().equals(password));
    }
}
