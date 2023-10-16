package ru.zenclass.ylab.liquibase;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.exception.MigrationException;

import java.sql.Connection;
import java.sql.Statement;


/**
 * Класс для запуска миграций базы данных с использованием Liquibase.
 * <p>
 * Предоставляет функциональность для выполнения миграций на основе файла changelog.xml в директории liquibase.
 * </p>
 */
@RequiredArgsConstructor
public class LiquibaseMigrationRunner {

    // Логгер для записи информации о выполнении миграций
    private final Logger log = LoggerFactory.getLogger(LiquibaseMigrationRunner.class);

    // Менеджер соединений с базой данных
    private final DatabaseConnectionManager connectionManager;

    /**
     * Запускает миграции на базе данных с использованием Liquibase.
     * <p>
     * Метод создает соединение с базой данных, устанавливает соответствующие параметры для Liquibase и
     * выполняет миграции, определенные в файле changelog.xml.
     * </p>
     *
     * @throws MigrationException если произошла ошибка при выполнении миграции.
     */
    public void runMigrations() {
        try (Connection connection = connectionManager.getConnection();
             Statement stmt = connection.createStatement()
        ) {
            //создаю схему migration для служебных таблиц liquibase
            stmt.execute("CREATE SCHEMA IF NOT EXISTS migration;");
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            //задаю схему в которой будут хранится служебные таблицы liquibase
            database.setLiquibaseSchemaName("migration");
            Liquibase liquibase = new Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            log.info("Миграции успешно выполнены!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new MigrationException("Ошибка: миграции не выполнены!");
        }
    }
}