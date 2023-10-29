package ru.zenclass.ylab.service;

import ru.zenclass.ylab.model.dto.LoginResponseDTO;
import ru.zenclass.ylab.model.dto.PlayerBalanceDTO;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;

import java.util.Optional;

/**
 * Интерфейс сервиса для работы с сущностями игрока {@link Player}.
 */
public interface PlayerService {

    /**
     * Найти игрока по его ID.
     *
     * @param id идентификатор игрока.
     * @return найденный игрок.
     */
    Player findPlayerById(Long id);

    /**
     * Обновить данные игрока.
     *
     * @param updatedPlayer обновленные данные игрока.
     */
    void updatePlayer(Player updatedPlayer);

    /**
     * Зарегистрировать нового игрока.
     *
     * @param registerPlayerDTO данные для регистрации.
     * @return DTO нового игрока.
     */
    PlayerDTO registerNewPlayer(RegisterPlayerDTO registerPlayerDTO);

    /**
     * Найти игрока по имени пользователя.
     *
     * @param username имя пользователя.
     * @return найденный игрок.
     */
    Optional<Player> findPlayerByUsername(String username);

    /**
     * Получить информацию о балансе игрока.
     *
     * @param player игрок.
     * @return информация о балансе в формате JSON.
     */
    PlayerBalanceDTO getPlayerBalanceInfo(Player player);


    LoginResponseDTO authenticateAndGenerateToken(RegisterPlayerDTO registerPlayerDTO);
    public Optional<Player> validateTokenAndGetPlayer(String token);
}