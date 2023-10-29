package ru.zenclass.ylab.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zenclass.ylab.exception.*;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(PlayerAlreadyExistException.class)
    public ResponseEntity<ExceptionDetails> handlePlayerAlreadyExistException() {
        ExceptionDetails exceptionDetails = new ExceptionDetails();
        exceptionDetails.setMessage("Игрок с таким именем уже существует");
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exceptionDetails);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionDetails> handleAuthenticationException() {
        ExceptionDetails exceptionDetails = new ExceptionDetails();
        exceptionDetails.setMessage("Ошибка авторизации, пожалуйста проверьте введенные данные");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(exceptionDetails);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionDetails> handleJwtException() {
        ExceptionDetails exceptionDetails = new ExceptionDetails();
        exceptionDetails.setMessage("Ошибка авторизации, токен отсутствует или недействителен");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(exceptionDetails);
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    public ResponseEntity<ExceptionDetails> handleNotEnoughMoneyException() {
        ExceptionDetails exceptionDetails = new ExceptionDetails();
        exceptionDetails.setMessage("Ошибка дебетовой транзакции, недостаточно денег на счете");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exceptionDetails);
    }
    @ExceptionHandler(NoTransactionsFoundException.class)
    public ResponseEntity<ExceptionDetails> handleNoTransactionsFoundException() {
        ExceptionDetails exceptionDetails = new ExceptionDetails();
        exceptionDetails.setMessage("У игрока еще нету истории транзакций");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exceptionDetails);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
