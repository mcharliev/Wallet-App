package ru.zenclass.ylab.repository;

import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerRepository {
    private final List<Player> players = new ArrayList<>();

    public void addPlayer(Player player) {
        players.add(player);
    }

    public List<Player> getAllPlayers(){
        return players;
    }

    public Player findPlayerById(String id) {
        for (Player player : players) {
            if (player.getId().equals(id)) {
                return player;
            }
        }
        throw new PlayerNotFoundException("Игрок с именем: " + id + " не найден!");
    }

}
