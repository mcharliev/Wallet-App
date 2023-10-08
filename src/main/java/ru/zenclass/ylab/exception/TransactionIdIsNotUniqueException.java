package ru.zenclass.ylab.exception;

public class TransactionIdIsNotUnique extends RuntimeException {
    public TransactionIdIsNotUnique(String message) {
        super(message);
    }
}
