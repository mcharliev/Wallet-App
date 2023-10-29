package ru.zenclass.ylab.service;

import ru.zenclass.ylab.model.dto.AmountDTO;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.dto.TransactionHistoryDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Интерфейс для работы с транзакциями игроков.
 */
public interface TransactionService {


    TransactionDTO addDebitTransaction(Player player,  AmountDTO amountDTO);



    TransactionDTO addCreditTransaction(Player player, AmountDTO amountDTO);


    TransactionHistoryDTO viewTransactionHistory(Player player);

}
