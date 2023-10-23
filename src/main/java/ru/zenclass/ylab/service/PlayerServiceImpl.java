package ru.zenclass.ylab.service;


import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.repository.PlayerRepository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Сервис для управления данными игрока.
 * Этот сервис предоставляет методы для выполнения основных операций, таких как поиск, обновление, регистрация и вход.
 */

public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

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
    public Optional<Player> registerPlayer(String username, String password) {
        Optional<Player> optPlayer = playerRepository.findPlayerByUsername(username);
        if (optPlayer.isEmpty()) {
            Player player = new Player();
            player.setUsername(username);
            player.setPassword(password);
            player.setBalance(BigDecimal.ZERO);
            playerRepository.addPlayer(player);
            return Optional.of(player);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Вход игрока в систему.
     *
     * @param username имя пользователя.
     * @param password пароль пользователя.
     * @return {@link Optional<Player>} игрока, если логин и пароль верные.
     */
    public Optional<Player> login(String username, String password) {
        return authenticatePlayer(username, password);
    }

    /**
     * Поиск игрока по имени пользователя.
     *
     * @param username имя пользователя.
     * @return {@link Optional<Player>} игрока, если он найден.
     */
    @Override
    public Optional<Player> findPlayerByUsername(String username) {
        return playerRepository.findPlayerByUsername(username);
    }

    /**
     * Получение информации о балансе игрока.
     *
     * @param player игрок, баланс которого необходимо получить.
     * @return строка с информацией о балансе в формате JSON.
     */
    public String getPlayerBalanceInfo(Player player) {
        return String.format(
                "{ \"message\": \"Текущий баланс игрока %s\", \"balance\": %s }",
                player.getUsername(),
                player.getBalance().toPlainString()
        );
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
