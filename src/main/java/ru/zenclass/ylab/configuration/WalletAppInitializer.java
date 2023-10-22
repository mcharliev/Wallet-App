package ru.zenclass.ylab.configuration;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.liquibase.LiquibaseMigrationRunner;

public class WalletAppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        LiquibaseMigrationRunner migrationRunner = new LiquibaseMigrationRunner(connectionManager);
        migrationRunner.runMigrations();
    }
}
