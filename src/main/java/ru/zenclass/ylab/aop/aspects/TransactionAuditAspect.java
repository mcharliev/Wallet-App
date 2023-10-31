package ru.zenclass.ylab.aop.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.enums.PlayerActionType;
import ru.zenclass.ylab.service.PlayerAuditService;

/**
 * Аспект для аудита транзакций с объектами типа {@link Transaction}.
 */
@Aspect
@Component
public class TransactionAuditAspect {

    private static final Logger log = LoggerFactory.getLogger(TransactionAuditAspect.class);

    private final PlayerAuditService playerAuditService;

    @Autowired
    public TransactionAuditAspect(PlayerAuditService playerAuditService) {
        this.playerAuditService = playerAuditService;
    }

    @Pointcut("execution(* ru.zenclass.ylab.service.impl.TransactionServiceImpl.addDebitTransaction(..))")
    public void debitTransactionMethod() {}

    @Around("debitTransactionMethod()")
    public Object logDebitTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Player player) {
            logAction(player.getUsername(), PlayerActionType.DEBIT_TRANSACTION_SUCCESS);
            playerAuditService.logPlayerAction(player.getId(), PlayerActionType.DEBIT_TRANSACTION_SUCCESS.toString(), createLogMessage(player.getUsername(), "успешно совершил дебетовую операцию"));
        }
        return result;
    }

    @Pointcut("execution(* ru.zenclass.ylab.service.impl.TransactionServiceImpl.addCreditTransaction(..))")
    public void creditTransactionMethod() {}

    @Around("creditTransactionMethod()")
    public Object logCreditTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Player player) {
            logAction(player.getUsername(), PlayerActionType.CREDIT_TRANSACTION_SUCCESS);
            playerAuditService.logPlayerAction(player.getId(), PlayerActionType.CREDIT_TRANSACTION_SUCCESS.toString(),
                    createLogMessage(player.getUsername(), "успешно совершил кредитную операцию"));
        }
        return result;
    }

    @Pointcut("execution(* ru.zenclass.ylab.service.impl.TransactionServiceImpl.viewTransactionHistory(..))")
    public void viewTransactionHistoryMethod() {}

    @Around("viewTransactionHistoryMethod()")
    public Object logViewTransactionHistory(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Player player) {
            logAction(player.getUsername(), PlayerActionType.VIEW_TRANSACTION_HISTORY);
            playerAuditService.logPlayerAction(player.getId(), PlayerActionType.VIEW_TRANSACTION_HISTORY.toString(),
                    createLogMessage(player.getUsername(), "просмотрел свою историю транзакций"));
        }
        return result;
    }

    private void logAction(String username, PlayerActionType actionType) {
        log.info(createLogMessage(username, actionType.getAction()));
    }

    private String createLogMessage(String username, String action) {
        return "Пользователь " + username + " " + action;
    }
}

