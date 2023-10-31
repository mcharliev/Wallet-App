package ru.zenclass.ylab.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.zenclass.ylab.aop.annotations.Loggable;
import ru.zenclass.ylab.model.entity.PlayerAudit;
import ru.zenclass.ylab.repository.PlayerAuditRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Loggable
@Repository
public class PlayerAuditRepositoryImpl implements PlayerAuditRepository {
    private final DataSource dataSource;
    private final Logger log = LoggerFactory.getLogger(PlayerAuditRepositoryImpl.class);

    @Autowired
    public PlayerAuditRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addPlayerAudit(PlayerAudit playerAudit) {
        String sql = "INSERT INTO wallet_service.audit_actions (player_id, action_type, action_data, details) VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, playerAudit.getPlayerId());
            preparedStatement.setString(2, playerAudit.getActionType());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(playerAudit.getActionDate()));
            preparedStatement.setString(4, playerAudit.getDetails());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Ошибка при добавлении записи аудита", e);
            throw new RuntimeException("Ошибка при добавлении записи аудита", e);
        }
    }

    public List<PlayerAudit> findAuditsByPlayerId(Long playerId) {
        String sql = "SELECT * FROM wallet_service.audit_actions WHERE player_id = ?";

        List<PlayerAudit> audits = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, playerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    audits.add(getPlayerAudit(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка при поиске записей аудита для игрока с ID: " + playerId, e);
            throw new RuntimeException("Ошибка при поиске записей аудита для игрока с ID: " + playerId, e);
        }

        return audits;
    }

    private PlayerAudit getPlayerAudit(ResultSet resultSet) throws SQLException {
        PlayerAudit audit = new PlayerAudit();
        audit.setId(resultSet.getLong("id"));
        audit.setPlayerId(resultSet.getLong("player_id"));
        audit.setActionType(resultSet.getString("action_type"));
        audit.setActionDate(resultSet.getTimestamp("action_data").toLocalDateTime());        audit.setDetails(resultSet.getString("details"));
        return audit;
    }
}
