package ru.zenclass.ylab.service.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Конфигурационный класс для настройки и создания источника данных (DataSource) для тестовых целей.
 * Этот класс использует настройки из файла application.properties для определения URL, имени пользователя и пароля
 * базы данных.
 * Использует библиотеку HikariCP для создания и настройки источника данных.
 */
@Configuration
public class TestDataSourceConfig {

//    /**
//     * URL базы данных, полученный из настроек приложения.
//     */
//    @Value("${spring.datasource.url}")
//    private String dbUrl;
//
//    /**
//     * Имя пользователя базы данных, полученное из настроек приложения.
//     */
//    @Value("${spring.datasource.username}")
//    private String dbUsername;
//
//    /**
//     * Пароль пользователя базы данных, полученный из настроек приложения.
//     */
//    @Value("${spring.datasource.password}")
//    private String dbPassword;
//
//    /**
//     * Создает и возвращает настроенный источник данных (DataSource) с использованием HikariCP.
//     *
//     * @return Источник данных {@link DataSource}
//     */
//    @Bean
//    public DataSource dataSource() {
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl(dbUrl);
//        dataSource.setUsername(dbUsername);
//        dataSource.setPassword(dbPassword);
//        return dataSource;
//    }
}
