package ru.zenclass.ylab.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.zenclass.ylab.model.entity.PlayerAudit;
import ru.zenclass.ylab.repository.PlayerAuditRepository;
import ru.zenclass.ylab.service.PlayerAuditService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlayerAuditServiceImpl implements PlayerAuditService {

    private final PlayerAuditRepository playerAuditRepository;

    @Autowired
    public PlayerAuditServiceImpl(PlayerAuditRepository playerAuditRepository) {
        this.playerAuditRepository = playerAuditRepository;
    }

    @Override
    public void logPlayerAction(Long playerId, String actionType, String details) {
        PlayerAudit audit = new PlayerAudit();
        audit.setPlayerId(playerId);
        audit.setActionType(actionType);
        audit.setDetails(details);
        audit.setActionDate(LocalDateTime.now());

        playerAuditRepository.addPlayerAudit(audit);
    }

    @Override
    public List<PlayerAudit> getAuditForPlayer(Long playerId) {
        return playerAuditRepository.findAuditsByPlayerId(playerId);
    }
}
