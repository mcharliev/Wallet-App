package ru.zenclass.ylab.service;

import ru.zenclass.ylab.model.entity.Player;

import java.util.Optional;

/**
 * Интерфейс сервиса для работы с сущностями игрока {@link Player}.
 */
public interface PlayerService {

    /**
     * Поиск игрока по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор игрока
     * @return найденная сущность игрока
     */
    Player findPlayerById(Long id);

    /**
     * Обновление данных игрока.
     *
     * @param updatedPlayer сущность игрока с обновленными данными
     */
    void updatePlayer(Player updatedPlayer);

    /**
     * Регистрация нового игрока в системе.
     *
     * @param username имя пользователя для регистрации
     * @param password пароль пользователя для регистрации
     * @return созданная сущность игрока {@link Optional Optional<Player>}
     * или {@link Optional#empty()} если регистрация не удалась
     */
    Optional<Player> registerPlayer(String username, String password);

    /**
     * Вход игрока в систему.
     *
     * @param username имя пользователя для входа
     * @param password пароль пользователя для входа
     * @return сущность авторизованного игрока {@link Optional Optional<Player>}
     * или {@link Optional#empty()} если авторизация не удалась
     */
    Optional<Player> login(String username, String password);

    /**
     * Поиск игрока по его имени пользователя.
     * @param username имя пользователя
     * @return найденная сущность игрока {@link Optional Optional<Player>}
     * или {@link Optional#empty()} если игрок с таким именем не найден
     */
    Optional<Player> findPlayerByUsername(String username);

}