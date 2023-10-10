package ru.zenclass.ylab.exception;

/**
 * Класс исключения, представляющий ситуацию, когда идентификатор транзакции не является уникальным.
 */
public class TransactionIdIsNotUniqueException extends RuntimeException {
    public TransactionIdIsNotUniqueException(String message) {
        super(message);
    }
}
