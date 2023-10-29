package ru.zenclass.ylab.service;

import ru.zenclass.ylab.model.entity.PlayerAudit;

import java.util.List;

public interface PlayerAuditService {

    void logPlayerAction(Long playerId, String actionType, String details);

    List<PlayerAudit> getAuditForPlayer(Long playerId);
}
