package ru.zenclass.ylab.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zenclass.ylab.audit.aop.AuditContract;
import ru.zenclass.ylab.service.PlayerAuditService;

@Service
public class AuditServiceWrapper implements AuditContract {
    private final PlayerAuditService playerAuditService;

    public AuditServiceWrapper(PlayerAuditService playerAuditService) {
        this.playerAuditService = playerAuditService;
    }

    @Transactional
    @Override
    public void logPlayerAction(Long playerId, String actionType, String message) {
        playerAuditService.logPlayerAction(playerId, actionType, message);
    }
}
