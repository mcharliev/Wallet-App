package ru.zenclass.ylab.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.zenclass.ylab.aop.annotation.Loggable;
import ru.zenclass.ylab.model.entity.PlayerAudit;
import ru.zenclass.ylab.repository.PlayerAuditRepository;
import ru.zenclass.ylab.service.PlayerAuditService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса для работы с аудитом действий игроков.
 */
@Service
@Loggable
public class PlayerAuditServiceImpl implements PlayerAuditService {

    private final PlayerAuditRepository playerAuditRepository;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param playerAuditRepository Репозиторий аудита игроков, тип {@link PlayerAuditRepository}.
     */
    @Autowired
    public PlayerAuditServiceImpl(PlayerAuditRepository playerAuditRepository) {
        this.playerAuditRepository = playerAuditRepository;
    }

    /**
     * Логирует действие игрока.
     *
     * @param playerId   Идентификатор игрока, для которого логируется действие, тип {@link Long}.
     * @param actionType Тип действия, тип {@link String}.
     * @param details    Детали действия, тип {@link String}.
     */
    @Override
    public void logPlayerAction(Long playerId, String actionType, String details) {
        PlayerAudit audit = new PlayerAudit();
        audit.setPlayerId(playerId);
        audit.setActionType(actionType);
        audit.setDetails(details);
        audit.setActionDate(LocalDateTime.now());

        playerAuditRepository.addPlayerAudit(audit);
    }

    /**
     * Получает аудит действий для игрока.
     *
     * @param playerId Идентификатор игрока, для которого запрашивается аудит, тип {@link Long}.
     * @return Список записей аудита для игрока, тип {@link List} {@link PlayerAudit}.
     */
    @Override
    public List<PlayerAudit> getAuditForPlayer(Long playerId) {
        return playerAuditRepository.findAuditsByPlayerId(playerId);
    }
}
