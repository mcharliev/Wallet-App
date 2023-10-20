package ru.zenclass.ylab.service;

import ru.zenclass.ylab.aop.annotations.Loggable;
import ru.zenclass.ylab.model.enums.ApplicationStatus;

@Loggable
public class HealthCheckService {

    public ApplicationStatus getApplicationStatus() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ApplicationStatus.UP;
    }
}
