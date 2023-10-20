package ru.zenclass.ylab.service;

import ru.zenclass.ylab.model.Player;

import java.util.Optional;

public interface PlayerService {
    Player findPlayerById(Long id);
    void updatePlayer(Player updatedPlayer);
    Optional<Player> registerPlayer(String username, String password);
    Optional<Player> login(String username, String password);

}