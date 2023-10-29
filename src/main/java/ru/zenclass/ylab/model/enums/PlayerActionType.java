package ru.zenclass.ylab.model.enums;

public enum PlayerActionType {
    REGISTRATION("REGISTRATION"),
    AUTHENTICATION("AUTHENTICATION"),
    BALANCE_CHECK("BALANCE_CHECK"),
    DEBIT_TRANSACTION_SUCCESS("DEBIT_TRANSACTION_SUCCESS"),
    CREDIT_TRANSACTION_SUCCESS("CREDIT_TRANSACTION_SUCCESS"),
    VIEW_TRANSACTION_HISTORY("VIEW_TRANSACTION_HISTORY");

    private final String action;

    PlayerActionType(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return action;
    }
}

