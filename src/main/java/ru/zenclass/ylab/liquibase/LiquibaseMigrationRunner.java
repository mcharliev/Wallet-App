package ru.zenclass.ylab.liquibase;

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.exception.MigrationException;
import ru.zenclass.ylab.service.TransactionService;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Класс-исполнитель для запуска миграций Liquibase.
 * <p>
 * Этот класс считывает конфигурацию базы данных из файла 'application.properties',
 * устанавливает соединение с базой данных и запускает миграции Liquibase.
 * </p>
 */
public class LiquibaseMigrationRunner {

    /**
     * Экземпляр для записи логов.
     */
    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    /**
     * Загружает свойства из файла 'application.properties'.
     *
     * @return Загруженные свойства.
     * @throws RuntimeException если произошла ошибка при чтении свойств.
     */
    private Properties loadProperties() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при загрузке свойств", e);
        }
        return properties;
    }

    /**
     * Запускает миграции Liquibase, используя конфигурацию базы данных из файла 'application.properties'.
     * <p>
     * Инициализирует соединение с базой данных, устанавливает схему для Liquibase,
     * затем запускает миграции из 'liquibase/changelog.xml'.
     * </p>
     * @throws MigrationException если в процессе миграции произошла ошибка.
     */
    public void runMigrations() {
        Properties properties = loadProperties();

        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        try {
            // Загрузка JDBC драйвера
            Class.forName(driver);
            // Установка соединения с базой данных
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                // Получение реализации базы данных, соответствующей соединению
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                // Установка имени схемы для таблиц Liquibase
                database.setLiquibaseSchemaName("migration");
                // Инициализация экземпляра Liquibase с указанием местоположения changelog и выполнение обновления для запуска миграций
                liquibase.Liquibase liquibase = new liquibase.Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), database);
                liquibase.update();
                log.info("Миграции успешно выполнены!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MigrationException("Ошибка: миграции не выполнены!");
        }
    }
}