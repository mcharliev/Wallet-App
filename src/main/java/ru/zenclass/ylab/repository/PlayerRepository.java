package ru.zenclass.ylab.repository;

import ru.zenclass.ylab.model.Player;

import java.util.Optional;

public interface PlayerRepository {
    /**
     * Добавляет нового игрока в базу данных.
     * @param player объект игрока, который следует добавить
     */
    void addPlayer(Player player);

    /**
     * Ищет игрока в базе данных по его ID.
     * @param id идентификатор игрока
     * @return {@link Optional} объект игрока, если он найден, иначе пустой {@link Optional}
     */
    Optional<Player> findPlayerById(Long id);

    /**
     * Ищет игрока в базе данных по его имени пользователя.
     * @param username имя игрока
     * @return {@link Optional} объект игрока, если он найден, иначе пустой {@link Optional}
     */
    Optional<Player> findPlayerByUsername(String username);

    /**
     * Обновляет информацию о балансе игрока в базе данных.
     * @param player объект игрока с новой информацией о балансе
     */
    void updatePlayer(Player player);

}

