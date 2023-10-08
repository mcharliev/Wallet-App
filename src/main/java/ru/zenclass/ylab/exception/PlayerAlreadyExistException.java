package ru.zenclass.ylab.exception;

public class PlayerAlreadyExistException extends RuntimeException {
    public PlayerAlreadyExistException(String message) {
        super(message);
    }
}
