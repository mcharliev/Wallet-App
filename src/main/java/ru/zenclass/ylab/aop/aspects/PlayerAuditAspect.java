package ru.zenclass.ylab.aop.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.model.dto.LoginResponseDTO;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.entity.Player;

/**
 * Аспект для аудита действий с объектами типа {@link Player}.
 */
@Aspect
public class PlayerAuditAspect {
    private static final Logger log = LoggerFactory.getLogger(PlayerAuditAspect.class);

    @Pointcut("execution(* ru.zenclass.ylab.service.PlayerServiceImpl.registerNewPlayer(..))")
    public void registerNewPlayer() {
    }

    @Around("registerNewPlayer()")
    public Object logRegistration(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result instanceof PlayerDTO) {
            PlayerDTO playerDTO = (PlayerDTO) result;
            log.info("Пользователь " + playerDTO.getUsername() + " успешно зарегистрировался");
        }
        return result;
    }

    @Pointcut("execution(* ru.zenclass.ylab.service.PlayerServiceImpl.authenticateAndGenerateToken(..))")
    public void authenticateAndGenerateToken() {
    }

    @Around("authenticateAndGenerateToken()")
    public Object logLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String username = (String) args[0];
        Object result = joinPoint.proceed();
        if (result instanceof LoginResponseDTO) {
            log.info("Пользователь " + username + " прошел авторизацию");
        } else {
            log.error("Ошибка авторизации пользователя с именем " + username);
        }
        return result;
    }

    @Pointcut("execution(* ru.zenclass.ylab.service.PlayerServiceImpl.getPlayerBalanceInfo(..))")
    public void getPlayerBalanceInfoMethod() {
    }

    @Around("getPlayerBalanceInfoMethod()")
    public Object logGetPlayerBalanceInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Player player = (Player) args[0];
        Object result = joinPoint.proceed();
        if (result instanceof String) {
            log.info("Запрос баланса для игрока " + player.getUsername() + ": " + result);
        }
        return result;
    }
}
