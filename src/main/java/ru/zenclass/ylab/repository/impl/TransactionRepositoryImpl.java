package ru.zenclass.ylab.repository.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.zenclass.ylab.aop.annotation.Loggable;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.enums.TransactionType;
import ru.zenclass.ylab.repository.TransactionRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для работы с транзакциями в базе данных.
 * Предоставляет функции для добавления транзакций и получения транзакций для определенного игрока.
 */
@Repository
@Loggable
public class TransactionRepositoryImpl implements TransactionRepository {

    private final DataSource dataSource;

    private final Logger log = LoggerFactory.getLogger(TransactionRepositoryImpl.class);

    @Autowired
    public TransactionRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Добавляет новую транзакцию в базу данных.
     *
     * @param transaction объект транзакции для добавления
     * @param playerId    идентификатор игрока, для которого добавляется транзакция
     */
    public void addTransaction(Transaction transaction, Long playerId) {

        String sql = "INSERT INTO wallet_service.transactions (id, type, amount, local_date_time, player_id) VALUES (nextval('wallet_service.transactions_seq'), ?, ?, ?, ?) RETURNING id";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, transaction.getType().name());
            preparedStatement.setBigDecimal(2, transaction.getAmount());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(transaction.getLocalDateTime()));
            preparedStatement.setLong(4, playerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    long generatedId = resultSet.getLong(1);
                    transaction.setId(generatedId);
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка при добавлении транзакции", e);
            throw new RuntimeException("Ошибка при добавлении транзакции", e);
        }
    }

    /**
     * Получает все транзакции, связанные с определенным игроком, из базы данных.
     *
     * @param playerId идентификатор игрока, для которого требуется получить транзакции
     * @return список транзакций {@link Transaction} для заданного игрока
     */
    public List<Transaction> getAllTransactionsByPlayerId(Long playerId) {
        String sql = "SELECT * FROM wallet_service.transactions WHERE player_id = ?";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, playerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setId(resultSet.getLong("id"));
                    transaction.setType(TransactionType.valueOf(resultSet.getString("type")));
                    transaction.setAmount(resultSet.getBigDecimal("amount"));
                    transaction.setLocalDateTime(resultSet.getTimestamp("local_date_time").toLocalDateTime());
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении всех транзакций для playerId: " + playerId, e);
            throw new RuntimeException("Ошибка при получении всех транзакций для playerId: " + playerId, e);
        }
        return transactions;
    }
}