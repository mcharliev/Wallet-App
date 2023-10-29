package ru.zenclass.ylab.controller;

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
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;

    }

    @PostMapping("/credit")
    public ResponseEntity<TransactionDTO> addCreditTransaction(
            @RequestAttribute("authenticatedPlayer") Player authenticatedPlayer,
            @RequestBody AmountDTO amountDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.addCreditTransaction(authenticatedPlayer, amountDTO));
    }

    @PostMapping("/debit")
    public ResponseEntity<TransactionDTO> addDebitTransaction(
            @RequestAttribute("authenticatedPlayer") Player authenticatedPlayer,
            @RequestBody AmountDTO amountDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.addDebitTransaction(authenticatedPlayer, amountDTO));
    }

    @GetMapping("/history")
    public ResponseEntity<TransactionHistoryDTO> viewTransactionHistory(
            @RequestAttribute("authenticatedPlayer") Player authenticatedPlayer) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.viewTransactionHistory(authenticatedPlayer));
    }
}
