package ru.zenclass.ylab.liquibase;

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
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
@AllArgsConstructor
public class LiquibaseMigrationRunner {

    /**
     * Экземпляр для записи логов.
     */
    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final DatabaseConnectionManager connectionManager;


    /**
     * Запускает миграции Liquibase, используя конфигурацию базы данных из файла 'application.properties'.
     * <p>
     * Инициализирует соединение с базой данных, устанавливает схему для Liquibase,
     * затем запускает миграции из 'liquibase/changelog.xml'.
     * </p>
     *
     * @throws MigrationException если в процессе миграции произошла ошибка.
     */
    public void runMigrations() {
        try (Connection connection = connectionManager.getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setLiquibaseSchemaName("migration");
            liquibase.Liquibase liquibase = new liquibase.Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            log.info("Миграции успешно выполнены!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new MigrationException("Ошибка: миграции не выполнены!");
        }
    }
}