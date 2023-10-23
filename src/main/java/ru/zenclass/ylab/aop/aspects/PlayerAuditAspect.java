package ru.zenclass.ylab.aop.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.model.entity.Player;

import java.util.Optional;

/**
 * Аспект для аудита действий с объектами типа {@link Player}.
 */
@Aspect
public class PlayerAuditAspect {
    private static final Logger log = LoggerFactory.getLogger(PlayerAuditAspect.class);

    /**
     * Определение точки среза для метода регистрации игрока.
     */
    @Pointcut("execution(* ru.zenclass.ylab.service.PlayerServiceImpl.registerPlayer(..))")
    public void registerPlayerMethod() {
    }

    /**
     * Логирование процесса регистрации игрока.
     *
     * @param joinPoint соединительная точка
     * @return результат выполнения метода, тип {@link Object}
     * @throws Throwable возможное исключение {@link Throwable}
     */
    @Around("registerPlayerMethod()")
    public Object logRegistration(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result instanceof Optional) {
            Optional<?> genericOpt = (Optional<?>) result;
            if (genericOpt.isEmpty()) {
                log.error("Ошибка регистрации пользователя. Пользователь с таким именем уже существует.");
            } else {
                Object possiblePlayer = genericOpt.get();
                if (possiblePlayer instanceof Player) {
                    Player player = (Player) possiblePlayer;
                    log.info("Пользователь " + player.getUsername() + " успешно зарегистрировался");
                }
            }
        }
        return result;
    }

    /**
     * Определение точки среза для метода авторизации игрока.
     */
    @Pointcut("execution(* ru.zenclass.ylab.service.PlayerServiceImpl.login(..))")
    public void loginMethod() {
    }

    /**
     * Логирование процесса авторизации игрока.
     *
     * @param joinPoint соединительная точка
     * @return результат выполнения метода, тип {@link Object}
     * @throws Throwable возможное исключение {@link Throwable}
     */
    @Around("loginMethod()")
    public Object logLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String username = (String) args[0];
        Object result = joinPoint.proceed();
        if (result instanceof Optional) {
            Optional<?> genericOpt = (Optional<?>) result;
            if (genericOpt.isEmpty()) {
                log.error("Ошибка авторизации пользователя с именем " + username);
            } else {
                log.info("Пользователь " + username + " прошел авторизацию");
            }
        }
        return result;
    }

    /**
     * Определение точки среза для метода получения баланса игрока.
     */
    @Pointcut("execution(* ru.zenclass.ylab.service.PlayerServiceImpl.getPlayerBalanceInfo(..))")
    public void getPlayerBalanceInfoMethod() {
    }

    /**
     * Логирование процесса получения информации о балансе игрока.
     *
     * @param joinPoint соединительная точка
     * @return результат выполнения метода, тип {@link Object}
     * @throws Throwable возможное исключение {@link Throwable}
     */
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
