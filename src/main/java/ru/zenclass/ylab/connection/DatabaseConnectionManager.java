package ru.zenclass.ylab.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Класс для управления подключениями к базе данных.
 * <p>
 * Он предоставляет методы для установления соединения с базой данных на основе свойств,
 * загруженных из файла properties.
 * </p>
 */

public class DatabaseConnectionManager {

    // Свойства для настройки подключения к базе данных
    private final Properties properties;

    public DatabaseConnectionManager() {
        this.properties = loadProperties();
    }
    public DatabaseConnectionManager(String url, String username, String password,String driver) {
        this.properties = new Properties();
        properties.setProperty("url", url);
        properties.setProperty("username", username);
        properties.setProperty("password", password);
        properties.setProperty("driver",driver);
    }

    /**
     * Загружает свойства из файла для настройки подключения к базе данных.
     *
     * @return Properties, содержащий параметры для подключения.
     * @throws RuntimeException если происходит ошибка при чтении файла свойств.
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/application.properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке свойств", e);
        }
        return props;
    }

    /**
     * Устанавливает соединение с базой данных на основе свойств, загруженных из файла.
     *
     * @return Connection - объект соединения с базой данных.
     * @throws ClassNotFoundException если драйвер базы данных не найден.
     * @throws SQLException           если происходит ошибка при установке соединения с базой данных.
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        // Регистрация драйвера JDBC
        Class.forName(driver);

        // Установка соединения с базой данных
        return DriverManager.getConnection(url, username, password);
    }
}

