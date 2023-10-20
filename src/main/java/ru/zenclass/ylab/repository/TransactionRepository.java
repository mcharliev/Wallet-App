package ru.zenclass.ylab.repository;


import ru.zenclass.ylab.model.entity.Transaction;
import java.util.List;

public interface TransactionRepository {

    /**
     * Добавляет новую транзакцию в базу данных.
     *
     * @param transaction объект транзакции для добавления
     * @param playerId идентификатор игрока, для которого добавляется транзакция
     */
    void addTransaction(Transaction transaction, Long playerId);

    /**
     * Получает все транзакции, связанные с определенным игроком, из базы данных.
     *
     * @param playerId идентификатор игрока, для которого требуется получить транзакции
     * @return список транзакций {@link Transaction} для заданного игрока
     */
    List<Transaction> getAllTransactionsByPlayerId(Long playerId);
}
