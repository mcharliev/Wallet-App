package ru.zenclass.ylab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ru.zenclass.ylab.aop.annotation.EnableLoggingAspect;

@SpringBootApplication
@EnableLoggingAspect
public class WalletServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalletServiceApplication.class, args);
    }
}
