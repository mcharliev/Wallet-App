package ru.zenclass.ylab.aop.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.zenclass.ylab.model.dto.LoginResponseDTO;
import ru.zenclass.ylab.model.dto.PlayerBalanceDTO;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.enums.PlayerActionType;
import ru.zenclass.ylab.service.PlayerAuditService;

/**
 * Аспект для аудита действий с объектами типа {@link Player}.
 */
@Aspect
@Component
public class PlayerAuditAspect {

    private static final Logger log = LoggerFactory.getLogger(PlayerAuditAspect.class);

    private final PlayerAuditService playerAuditService;

    @Autowired
    public PlayerAuditAspect(PlayerAuditService playerAuditService) {
        this.playerAuditService = playerAuditService;
    }

    @Pointcut("execution(* ru.zenclass.ylab.service.impl.PlayerServiceImpl.registerNewPlayer(..))")
    public void registerNewPlayer() {
    }

    @Around("registerNewPlayer()")
    public Object logRegistration(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result instanceof PlayerDTO playerDTO) {
            logAction(playerDTO.getUsername(), PlayerActionType.REGISTRATION);
            playerAuditService.logPlayerAction(playerDTO.getId(), PlayerActionType.REGISTRATION.toString(), createLogMessage(playerDTO.getUsername(), "успешно зарегистрировался"));
        }
        return result;
    }

    @Pointcut("execution(* ru.zenclass.ylab.service.impl.PlayerServiceImpl.authenticateAndGenerateToken(..))")
    public void authenticateAndGenerateToken() {
    }

    @Around("authenticateAndGenerateToken()")
    public Object logLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String username = null;
        if (args[0] instanceof RegisterPlayerDTO registerPlayerDTO) {
            username = registerPlayerDTO.getUsername();
        }

        Object result = joinPoint.proceed();
        if (result instanceof LoginResponseDTO responseDTO) {
            if (responseDTO.getPlayer() != null) {
                Long playerId = responseDTO.getPlayer().getId();
                if (username != null) {
                    logAction(username, PlayerActionType.AUTHENTICATION);
                    playerAuditService.logPlayerAction(playerId, PlayerActionType.AUTHENTICATION.toString(), createLogMessage(username, "прошел авторизацию"));
                }
            } else if (username != null) {
                log.error("Ошибка авторизации пользователя с именем " + username);
            }
        }
        return result;
    }

    @Pointcut("execution(* ru.zenclass.ylab.service.impl.PlayerServiceImpl.getPlayerBalanceInfo(..))")
    public void getPlayerBalanceInfoMethod() {
    }

    @Around("getPlayerBalanceInfoMethod()")
    public Object logGetPlayerBalanceInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Player player && result instanceof PlayerBalanceDTO) {
            logAction(player.getUsername(), PlayerActionType.BALANCE_CHECK);
            playerAuditService.logPlayerAction(player.getId(), PlayerActionType.BALANCE_CHECK.toString(), createLogMessage(player.getUsername(), "сделал запрос баланса"));
        }
        return result;
    }

    private void logAction(String username, PlayerActionType actionType) {
        switch (actionType) {
            case REGISTRATION -> log.info(createLogMessage(username, "успешно зарегистрировался"));
            case AUTHENTICATION -> log.info(createLogMessage(username, "прошел авторизацию"));
            case BALANCE_CHECK -> log.info(createLogMessage(username, "сделал запрос баланса"));
        }
    }

    private String createLogMessage(String username, String action) {
        return "Пользователь " + username + " " + action;
    }
}
