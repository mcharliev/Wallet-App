package ru.zenclass.ylab.aop.annotation;

import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.context.annotation.Import;
import ru.zenclass.ylab.aop.configuration.LoggableAspectConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, которая включает аспект логирования.
 * Необходима для добавления к конфигурационному классу Spring
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(LoggableAspectConfiguration.class)
public @interface EnableLoggingAspect {
}
