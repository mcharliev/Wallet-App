package ru.zenclass.ylab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.zenclass.ylab.aop.annotation.EnableLoggingAspect;

/**
 * Основной класс для запуска приложения WalletService.
 * Этот класс содержит главный метод для запуска Spring Boot приложения.
 * Используется аннотация {@link SpringBootApplication}
 * Также класс аннотирован с {@link EnableLoggingAspect}, что позволяет активировать
 * аспект логирования, определенный в модуле логирования. Логирование будет применяться
 * ко всем методам, аннотированным с {@code @Loggable} в контексте данного приложения.
 */
@SpringBootApplication
@EnableLoggingAspect
public class WalletServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalletServiceApplication.class, args);
    }
}
