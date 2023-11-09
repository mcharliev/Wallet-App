package ru.zenclass.ylab.audit.aop;

/**
 * Интерфейс для регистрации действий игрока для аудита
 */
public interface AuditContract {
    /**
     * Регистрирует действие игрока в аудит-логе.
     *
     * @param playerId   Идентификатор игрока, выполнившего действие.
     * @param actionType Тип действия, которое было выполнено (например, "вход").
     * @param message    Сообщение с дополнительной информацией о действии.
     */
    void logPlayerAction(Long playerId, String actionType, String message);
}
