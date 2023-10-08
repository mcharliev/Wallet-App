package ru.zenclass.ylab.exception;

public class TransactionIdIsNotUniqueException extends RuntimeException {
    public TransactionIdIsNotUniqueException(String message) {
        super(message);
    }
}
