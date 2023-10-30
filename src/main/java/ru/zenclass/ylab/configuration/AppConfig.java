package ru.zenclass.ylab.configuration;

import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import ru.zenclass.ylab.util.JwtUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "ru.zenclass.ylab")
@PropertySources({
        @PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
})
public class AppConfig {

    @Value("${database.driver}")
    private String dbDriver;

    @Value("${database.url}")
    private String dbUrl;

    @Value("${database.username}")
    private String dbUsername;

    @Value("${database.password}")
    private String dbPassword;

    @Value("${database.changeLogLog}")
    private String liquibaseChangeLog;

    @Value("${database.liquibaseSchema}")
    private String liquibaseSchema;

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setJdbcUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS migration;");
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось создать схему migration", e);
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibaseChangeLog);
        liquibase.setDefaultSchema(liquibaseSchema);
        return liquibase;
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSecretKey);
    }
}
