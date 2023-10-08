package ru.zenclass.ylab.service;

import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.repository.PlayerRepository;

import java.util.List;

public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void addPlayer(Player player) {
        playerRepository.addPlayer(player);
    }

    public List<Player> getAllPlayers() {
        return playerRepository.getAllPlayers();
    }

    public Player findPlayerById(String id) {
        return playerRepository.findPlayerById(id);
    }

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
