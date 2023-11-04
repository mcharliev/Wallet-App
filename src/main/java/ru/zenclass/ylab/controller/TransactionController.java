package ru.zenclass.ylab.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zenclass.ylab.model.dto.AmountDTO;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.dto.TransactionHistoryDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.service.TransactionService;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Управление транзакциями")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/credit")
    @Operation(summary = "Добавление кредитной транзакции для аутентифицированного игрока")
    @ApiResponses(value = {
            @ApiResponse(responseCode  = "201", description = "Кредитная транзакция успешно создана"),
            @ApiResponse(responseCode  = "400", description = "Неверные входные данные (не прошли валидацию)"),
            @ApiResponse(responseCode  = "401", description = "Учетные данные игрока неверны, или токен JWT неверен или истек"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TransactionDTO> addCreditTransaction(
            @Parameter(description = "Аутентифицированный игрок", required = true)
            @RequestAttribute("authenticatedPlayer") Player authenticatedPlayer,
            @Parameter(description = "Сумма для транзакции", required = true)
            @RequestBody AmountDTO amountDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.addCreditTransaction(authenticatedPlayer, amountDTO));
    }

    @PostMapping("/debit")
    @Operation(summary = "Добавление дебетовой транзакции для аутентифицированного игрока")
    @ApiResponses(value = {
            @ApiResponse(responseCode  = "201", description = "Дебетовая транзакция успешно создана"),
            @ApiResponse(responseCode  = "400", description = "Неверные входные данные (не прошли валидацию)"),
            @ApiResponse(responseCode  = "401", description = "Учетные данные игрока неверны, или токен JWT неверен или истек"),
            @ApiResponse(responseCode  = "409", description = "Недостаточно средств для осуществления транзакции"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TransactionDTO> addDebitTransaction(
            @Parameter(description = "Аутентифицированный игрок", required = true)
            @RequestAttribute("authenticatedPlayer") Player authenticatedPlayer,
            @Parameter(description = "Сумма для транзакции", required = true)
            @RequestBody AmountDTO amountDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.addDebitTransaction(authenticatedPlayer, amountDTO));
    }

    @GetMapping("/history")
    @Operation(summary = "Просмотр истории транзакций аутентифицированного игрока")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "История транзакций успешно получена"),
            @ApiResponse(responseCode  = "401", description = "Учетные данные игрока неверны, или токен JWT неверен или истек"),
            @ApiResponse(responseCode = "404", description = "У игрока нет истории транзакций"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TransactionHistoryDTO> viewTransactionHistory(
            @Parameter(description = "Аутентифицированный игрок", required = true)
            @RequestAttribute("authenticatedPlayer") Player authenticatedPlayer) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionService.viewTransactionHistory(authenticatedPlayer));
    }

}
