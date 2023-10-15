package ru.zenclass.ylab.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DatabaseConnectionManager {

    private final Properties properties;

    public DatabaseConnectionManager() {
        properties = loadProperties();
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/application.properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке свойств", e);
        }
        return props;
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
}

