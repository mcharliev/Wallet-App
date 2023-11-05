package ru.zenclass.ylab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.zenclass.ylab.aop.annotation.EnableLoggingAspect;

@SpringBootApplication
@EnableLoggingAspect
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"ru.zenclass.ylab.audit"})
public class WalletServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalletServiceApplication.class, args);
    }
}
