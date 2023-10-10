package ru.zenclass.ylab.exception;

/**
 * Класс исключения, представляющий ситуацию, когда игрок с заданным идентификатором не найден.
 */
public class PlayerNotFoundException extends RuntimeException {

    /**
     * Конструктор с сообщением об ошибке.
     * @param message Сообщение об ошибке.
     */
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
