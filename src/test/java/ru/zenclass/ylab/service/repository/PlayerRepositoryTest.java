package ru.zenclass.ylab.service.repository;

import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.zenclass.ylab.configuration.AppConfig;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.service.config.TestDataSourceConfig;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {AppConfig.class, TestDataSourceConfig.class})
@Testcontainers
@WebAppConfiguration
public class PlayerRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlayerRepository repository;
    private Player player;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.username", postgres::getUsername);

    }

    @BeforeEach
    void setUp() {
        SpringLiquibase liquibase = applicationContext.getBean(SpringLiquibase.class);
        try {
            liquibase.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при запуске миграций Liquibase", e);
        }
        player = new Player();
        player.setUsername("testUser");
        player.setPassword("testPassword");
        player.setBalance(new BigDecimal("100.00"));
    }

    @Test
    void testAddPlayer() {
        repository.addPlayer(player);

        assertThat(player.getId()).isNotNull();
    }

    @Test
    void testFindPlayerByUsername() {
        repository.addPlayer(player);

        Optional<Player> foundPlayerOpt = repository.findPlayerByUsername(player.getUsername());
        assertThat(foundPlayerOpt).isPresent();

        Player foundPlayer = foundPlayerOpt.get();
        assertThat(foundPlayer.getUsername()).isEqualTo(player.getUsername());
        assertThat(foundPlayer.getPassword()).isEqualTo(player.getPassword());
        assertThat(foundPlayer.getBalance()).isEqualByComparingTo(player.getBalance());
    }

    @Test
    void testFindNonExistentPlayerByUsername() {
        Optional<Player> notFoundPlayerOpt = repository.findPlayerByUsername("nonExistentUsername");
        assertThat(notFoundPlayerOpt).isNotPresent();
    }

    @Test
    void testUpdatePlayer() {
        repository.addPlayer(player);

        player.setBalance(new BigDecimal("400.00"));
        repository.updatePlayer(player);

        Optional<Player> updatedPlayerOpt = repository.findPlayerById(player.getId());
        assertThat(updatedPlayerOpt).isPresent();
        assertThat(updatedPlayerOpt.get().getBalance()).isEqualByComparingTo(new BigDecimal("400.00"));
    }

    @Test
    void testFindPlayerById() {
        repository.addPlayer(player);

        Optional<Player> foundPlayerOpt = repository.findPlayerById(player.getId());
        assertThat(foundPlayerOpt).isPresent();

        Player foundPlayer = foundPlayerOpt.get();
        assertThat(foundPlayer.getUsername()).isEqualTo(player.getUsername());
        assertThat(foundPlayer.getPassword()).isEqualTo(player.getPassword());
        assertThat(foundPlayer.getBalance()).isEqualByComparingTo(player.getBalance());
    }
}
