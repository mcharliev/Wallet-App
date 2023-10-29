package ru.zenclass.ylab.model.enums;

public enum PlayerActionType {
    REGISTRATION("REGISTRATION"),
    AUTHENTICATION("AUTHENTICATION"),
    BALANCE_CHECK("BALANCE_CHECK");

    private final String action;

    PlayerActionType(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }
}
