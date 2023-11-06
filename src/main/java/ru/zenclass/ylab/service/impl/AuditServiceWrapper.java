package ru.zenclass.ylab.service.impl;

import org.springframework.stereotype.Service;
import ru.zenclass.ylab.audit.aop.AuditContract;
import ru.zenclass.ylab.service.PlayerAuditService;

/**
 * Сервис-обертка для аудита действий игрока.
 * Реализует интерфейс {@link AuditContract} и делегирует вызовы метода logPlayerAction
 * сервису {@link PlayerAuditService}.
 */
@Service
public class AuditServiceWrapper implements AuditContract {
    private final PlayerAuditService playerAuditService;

    /**
     * Конструктор для создания экземпляра AuditServiceWrapper.
     *
     * @param playerAuditService Сервис аудита действий игрока.
     */
    public AuditServiceWrapper(PlayerAuditService playerAuditService) {
        this.playerAuditService = playerAuditService;
    }

    /**
     * Записывает действие игрока в аудит-лог.
     *
     * @param playerId   Идентификатор игрока.
     * @param actionType Тип действия игрока.
     * @param message    Сообщение для логирования.
     */
    @Override
    public void logPlayerAction(Long playerId, String actionType, String message) {
        playerAuditService.logPlayerAction(playerId, actionType, message);
    }
}
