package ru.zenclass.ylab.aop.configuration;

import org.springframework.context.annotation.Bean;
import ru.zenclass.ylab.aop.aspects.LoggableAspect;


/**
 * Конфигурация для предоставления аспекта логирования в контексте приложения Spring.
 * <p>
 * Аспект активируется только когда на классе конфигурации присутствует
 * аннотация {@code @EnableLoggingAspect}.
 * </p>
 */
public class LoggableAspectConfiguration {

    /**
     * Создает и возвращает экземпляр {@link LoggableAspect}.
     * <p>
     * Этот бин будет перехватывать вызовы методов, аннотированные {@code @Loggable},
     * и логировать ключевые моменты выполнения методов, такие как начало и окончание вызова,
     * а также продолжительность выполнения.
     * </p>
     *
     * @return новый экземпляр {@link LoggableAspect} готовый к использованию в контексте Spring.
     */
    @Bean
    public LoggableAspect loggableAspect() {
        return new LoggableAspect();
    }
}
