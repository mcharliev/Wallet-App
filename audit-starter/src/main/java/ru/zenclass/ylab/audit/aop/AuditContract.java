package ru.zenclass.ylab.audit.aop;

public interface AuditContract {
    void logPlayerAction(Long playerId, String actionType, String message);
}
