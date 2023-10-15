package ru.zenclass.ylab.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.service.PlayerService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления данными игроков в базе данных.
 * <p>
 * PlayerRepository предоставляет методы для выполнения основных операций CRUD (создание, чтение, обновление, удаление)
 * для объектов Player, хранящихся в базе данных.
 * </p>
 */
@RequiredArgsConstructor
public class PlayerRepository {

    /**
     * Менеджер соединения с базой данных, используется для получения соединений с БД.
     */
    private final DatabaseConnectionManager connectionManager;
    private final Logger log = LoggerFactory.getLogger(PlayerRepository.class);


    /**
     * Добавляет нового игрока в базу данных.
     *
     * @param player объект игрока для добавления.
     * @throws RuntimeException при возникновении ошибок во время выполнения SQL-запроса.
     */
    public void addPlayer(Player player) {
        //SQL-запрос для добавление игрока в таблицу players используем players_seq.NEXTVAL для генерации ID
        String sql = "INSERT INTO wallet_service.players (id, username, password, balance) VALUES (nextval('wallet_service.players_seq'), ?, ?, ?) RETURNING id";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Устанавливаем только значения username, password и balance
            preparedStatement.setString(1, player.getUsername());
            preparedStatement.setString(2, player.getPassword());
            preparedStatement.setBigDecimal(3, player.getBalance());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Получаем сгенерированный ID и устанавливаем его для объекта player
                    long generatedId = resultSet.getLong(1);
                    player.setId(generatedId);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка при добавлении игрока", e);
        }
    }

    /**
     * Извлекает список всех игроков из базы данных.
     *
     * @return список всех игроков.
     * @throws RuntimeException при возникновении ошибок во время выполнения SQL-запроса.
     */
    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        // SQL-запрос для получения всех игроков из таблицы players
        String sql = "SELECT * FROM wallet_service.players";

// Создание соединения с базой данных, подготовка и выполнение SQL-запроса
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Обработка результатов запроса
            while (resultSet.next()) {
                Player player = new Player();
                player.setId(resultSet.getLong("id"));
                player.setUsername(resultSet.getString("username"));
                player.setPassword(resultSet.getString("password"));
                player.setBalance(resultSet.getBigDecimal("balance"));
                players.add(player);
            }

        } catch (SQLException | ClassNotFoundException e) {
            // Обработка возможных исключений при работе с базой данных
            throw new RuntimeException("Ошибка при получении списка игроков", e);
        }
        return players;
    }

    /**
     * Ищет и возвращает игрока по его идентификатору из базы данных.
     *
     * @param id идентификатор игрока для поиска.
     * @return найденный игрок.
     * @throws PlayerNotFoundException если игрок с указанным идентификатором не найден.
     * @throws RuntimeException        при возникновении ошибок во время выполнения SQL-запроса.
     */
    public Player findPlayerById(Long id) {
        // SQL-запрос для поиска игрока по идентификатору
        String sql = "SELECT * FROM wallet_service.players WHERE id = ?";

       // Создание соединения с базой данных и подготовка SQL-запроса для выполнения
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Установка параметра id для SQL-запроса
            preparedStatement.setLong(1, id);

            // Выполнение запроса и получение результатов
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Player player = new Player();
                    player.setId(resultSet.getLong("id"));
                    player.setUsername(resultSet.getString("username"));
                    player.setPassword(resultSet.getString("password"));
                    player.setBalance(resultSet.getBigDecimal("balance"));
                    return player;
                } else {
                    // Если игрок не найден, выбросить исключение
                    throw new PlayerNotFoundException("Игрок с идентификатором: " + id + " не найден!");
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            // Обработка возможных исключений при работе с базой данных
            throw new RuntimeException("Ошибка при поиске игрока", e);
        }
    }
    /**
     * Ищет и возвращает игрока по его имени (username) из базы данных.
     *
     * @param username имя игрока для поиска.
     * @return Optional, содержащий найденного игрока или пустой, если игрок не найден.
     * @throws RuntimeException при возникновении ошибок во время выполнения SQL-запроса.
     */
    public Optional<Player> findPlayerByUsername(String username) {
        // SQL-запрос для поиска игрока по имени (username)
        String sql = "SELECT * FROM wallet_service.players WHERE username = ?";

        // Создание соединения с базой данных и подготовка SQL-запроса для выполнения
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Установка параметра username для SQL-запроса
            preparedStatement.setString(1, username);

            // Выполнение запроса и получение результатов
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Player player = new Player();
                    player.setId(resultSet.getLong("id"));
                    player.setUsername(resultSet.getString("username"));
                    player.setPassword(resultSet.getString("password"));
                    player.setBalance(resultSet.getBigDecimal("balance"));
                    return Optional.of(player);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            // Логирование ошибки
            log.error("Ошибка при поиске игрока по имени: " + username, e);
            // Выбрасываем исключение дальше, чтобы уведомить вызывающий метод о проблеме
            throw new RuntimeException("Ошибка при поиске игрока по имени: " + username, e);
        }
        return Optional.empty(); // Если игрок не найден
    }
}
