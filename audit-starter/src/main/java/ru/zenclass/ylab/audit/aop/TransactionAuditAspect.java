package ru.zenclass.ylab.audit.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.enums.PlayerActionType;


/**
 * Компонент для аудита действий игрока с транзакциями
 */
@Aspect
@Component
public class TransactionAuditAspect {

    private static final Logger log = LoggerFactory.getLogger(TransactionAuditAspect.class);

    private final AuditContract auditContract;

    public TransactionAuditAspect(AuditContract auditContract) {
        this.auditContract = auditContract;
    }

    /**
     * Точка среза для метода addDebitTransaction
     */
    @Pointcut("execution(* ru.zenclass.ylab.service.impl.TransactionServiceImpl.addDebitTransaction(..))")
    public void debitTransactionMethod() {}

    /**
     * Аспект для аудита дебетовых транзакций.
     *
     * @param joinPoint Присоединяемая точка выполнения.
     * @return Результат выполнения метода, тип {@link Object}.
     * @throws Throwable возможное исключение, тип {@link Throwable}.
     */
    @Around("debitTransactionMethod()")
    public Object logDebitTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Player player) {
            String actionMessage = "успешно совершил дебетовую операцию";
            log.info(createLogMessage(player.getUsername(), actionMessage));
            auditContract.logPlayerAction(player.getId(), PlayerActionType.DEBIT_TRANSACTION_SUCCESS.toString(),
                    createLogMessage(player.getUsername(), actionMessage));
        }
        return result;
    }

    /**
     * Точка среза для метода addCreditTransaction
     */
    @Pointcut("execution(* ru.zenclass.ylab.service.impl.TransactionServiceImpl.addCreditTransaction(..))")
    public void creditTransactionMethod() {}

    /**
     * Аспект для аудита кредитных транзакций.
     *
     * @param joinPoint Присоединяемая точка выполнения.
     * @return Результат выполнения метода, тип {@link Object}.
     * @throws Throwable возможное исключение, тип {@link Throwable}.
     */
    @Around("creditTransactionMethod()")
    public Object logCreditTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Player player) {
            String actionMessage = "успешно совершил кредитную операцию";
            log.info(createLogMessage(player.getUsername(), actionMessage));
            auditContract.logPlayerAction(player.getId(), PlayerActionType.CREDIT_TRANSACTION_SUCCESS.toString(),
                    createLogMessage(player.getUsername(), actionMessage));
        }
        return result;
    }

    /**
     * Точка среза для метода viewTransactionHistory
     */
    @Pointcut("execution(* ru.zenclass.ylab.service.impl.TransactionServiceImpl.viewTransactionHistory(..))")
    public void viewTransactionHistoryMethod() {}

    /**
     * Аспект для аудита просмотра истории транзакций.
     *
     * @param joinPoint Присоединяемая точка выполнения.
     * @return Результат выполнения метода, тип {@link Object}.
     * @throws Throwable возможное исключение, тип {@link Throwable}.
     */
    @Around("viewTransactionHistoryMethod()")
    public Object logViewTransactionHistory(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Player player) {
            String actionMessage = "просмотрел свою историю транзакций";
            log.info(createLogMessage(player.getUsername(), actionMessage));
            auditContract.logPlayerAction(player.getId(), PlayerActionType.VIEW_TRANSACTION_HISTORY.toString(),
                    createLogMessage(player.getUsername(), actionMessage));
        }
        return result;
    }


    /**
     * Метод для создания сообщения в логе.
     *
     * @param username Имя пользователя.
     * @param action   Действие игрока.
     * @return Сообщение для лога.
     */
    private String createLogMessage(String username, String action) {
        return "Пользователь " + username + " " + action;
    }
}

