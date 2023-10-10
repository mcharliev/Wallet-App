package ru.zenclass.ylab.repository;

import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для хранения данных игроков.
 */
public class PlayerRepository {
    private final List<Player> players = new ArrayList<>();

    /**
     * Добавляет игрока в репозиторий.
     * @param player Игрок для добавления.
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Возвращает список всех игроков.
     * @return Список всех игроков.
     */
    public List<Player> getAllPlayers(){
        return players;
    }

    /**
     * Находит игрока по его идентификатору.
     * @param id Идентификатор игрока для поиска.
     * @return Найденный игрок.
     * @throws PlayerNotFoundException если игрок не найден.
     */
    public Player findPlayerById(String id) {
        for (Player player : players) {
            if (player.getId().equals(id)) {
                return player;
            }
        }
        throw new PlayerNotFoundException("Игрок с именем: " + id + " не найден!");
    }
}
