package ru.zenclass.ylab.repository;

import ru.zenclass.ylab.model.entity.PlayerAudit;

import java.util.List;

public interface PlayerAuditRepository {

    void addPlayerAudit(PlayerAudit playerAudit);

    List<PlayerAudit> findAuditsByPlayerId(Long playerId);
}
