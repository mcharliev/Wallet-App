package ru.zenclass.ylab.exception;

public class AlreadyExistTransactionException extends RuntimeException {
    public AlreadyExistTransactionException(String message) {
        super(message);
    }
}
