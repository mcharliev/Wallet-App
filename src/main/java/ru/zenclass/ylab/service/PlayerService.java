package ru.zenclass.ylab.service;

import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.repository.PlayerRepository;

import java.util.List;

/**
 * Сервис для управления данными игроков.
 */
public class PlayerService {
    private final PlayerRepository playerRepository;

    /**
     * Конструктор класса PlayerService.
     *
     * @param playerRepository Репозиторий для хранения данных игроков.
     */
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

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
    public Player findPlayerById(String id) {
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
            if (existingPlayer.getId().equals(updatedPlayer.getId())) {
                players.set(i, updatedPlayer);
            }
        }
    }

}
