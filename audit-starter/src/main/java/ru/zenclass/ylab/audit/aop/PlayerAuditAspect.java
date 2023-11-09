package ru.zenclass.ylab.audit.aop;

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

import java.util.Arrays;


/**
 * Компонент для аудита действий игрока.
 */
@Aspect
@Component
public class PlayerAuditAspect {

    private static final Logger log = LoggerFactory.getLogger(PlayerAuditAspect.class);

    private final AuditContract auditContract;

    @Autowired
    public PlayerAuditAspect(AuditContract auditContract) {
        this.auditContract = auditContract;
    }

    /**
     * Точка среза для метода registerNewPlayer.
     */
    @Pointcut("execution(* ru.zenclass.ylab.service.impl.PlayerServiceImpl.registerNewPlayer(..))")
    public void registerNewPlayer() {
    }

    /**
     * Аспект для аудита регистрации игрока.
     *
     * @param joinPoint Присоединяемая точка выполнения.
     * @return Результат выполнения метода. Возвращается объект типа {@link PlayerDTO}.
     * @throws Throwable Возможное исключение, тип {@link Throwable}.
     */
    @Around("registerNewPlayer()")
    public Object logRegistration(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result instanceof PlayerDTO playerDTO) {
            logAction(playerDTO.getUsername(), PlayerActionType.REGISTRATION);
            auditContract.logPlayerAction(playerDTO.getId(), PlayerActionType.REGISTRATION.toString(), createLogMessage(playerDTO.getUsername(), "успешно зарегистрировался"));
        }
        return result;
    }

    /**
     * Точка среза для метода authenticateAndGenerateToken.
     */
    @Pointcut("execution(* ru.zenclass.ylab.service.impl.PlayerServiceImpl.authenticateAndGenerateToken(..))")
    public void authenticateAndGenerateToken() {
    }

    /**
     * Аспект для аудита аутентификации игрока.
     *
     * @param joinPoint Присоединяемая точка выполнения.
     * @return Результат выполнения метода. Возвращается объект типа {@link LoginResponseDTO}.
     * @throws Throwable Возможное исключение, тип {@link Throwable}.
     */
    @Around("authenticateAndGenerateToken()")
    public Object logLogin(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();
        log.info("Аргументы метода: " + Arrays.toString(args));
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
                    auditContract.logPlayerAction(playerId, PlayerActionType.AUTHENTICATION.toString(), createLogMessage(username, "прошел авторизацию"));
                }
            } else if (username != null) {
                log.error("Ошибка авторизации пользователя с именем " + username);
            }
        }
        return result;
    }

    /**
     * Точка среза для метода getPlayerBalanceInfo.
     */
    @Pointcut("execution(* ru.zenclass.ylab.service.impl.PlayerServiceImpl.getPlayerBalanceInfo(..))")
    public void getPlayerBalanceInfoMethod() {
    }

    /**
     * Аспект для аудита запроса баланса игроком.
     *
     * @param joinPoint Присоединяемая точка выполнения.
     * @return Результат выполнения метода. Возвращается объект типа {@link PlayerBalanceDTO}.
     * @throws Throwable Возможное исключение, тип {@link Throwable}.
     */
    @Around("getPlayerBalanceInfoMethod()")
    public Object logGetPlayerBalanceInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Player player && result instanceof PlayerBalanceDTO) {
            logAction(player.getUsername(), PlayerActionType.BALANCE_CHECK);
            auditContract.logPlayerAction(player.getId(), PlayerActionType.BALANCE_CHECK.toString(), createLogMessage(player.getUsername(), "сделал запрос баланса"));
        }
        return result;
    }

    /**
     * Логирует действие игрока.
     *
     * @param username   Имя пользователя.
     * @param actionType Тип действия пользователя (регистрация, аутентификация, запрос баланса).
     */
    private void logAction(String username, PlayerActionType actionType) {
        switch (actionType) {
            case REGISTRATION -> log.info(createLogMessage(username, "успешно зарегистрировался"));
            case AUTHENTICATION -> log.info(createLogMessage(username, "прошел авторизацию"));
            case BALANCE_CHECK -> log.info(createLogMessage(username, "сделал запрос баланса"));
        }
    }

    /**
     * Создает сообщение действий игрока
     *
     * @param username Имя пользователя.
     * @param action   Действие пользователя.
     * @return Строка с сообщением для логирования.
     */
    private String createLogMessage(String username, String action) {
        return "Пользователь " + username + " " + action;
    }
}
