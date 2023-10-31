package ru.zenclass.ylab.service.repository;

import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
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

import static org.junit.jupiter.api.Assertions.*;

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
    }

    @Test
    void testAddPlayer() {
        Player player = new Player();
        player.setId(1L);
        player.setUsername("testUser");
        player.setPassword("testPassword");
        player.setBalance(new BigDecimal("100.00"));

        repository.addPlayer(player);

        assertNotNull(player.getId());
    }

    @Test
    void testFindPlayerByUsername() {
        Player player = new Player();
        player.setUsername("userByUsername");
        player.setPassword("passwordByUsername");
        player.setBalance(new BigDecimal("200.00"));

        repository.addPlayer(player);

        Optional<Player> foundPlayerOpt = repository.findPlayerByUsername(player.getUsername());
        assertTrue(foundPlayerOpt.isPresent());

        Player foundPlayer = foundPlayerOpt.get();
        assertEquals(player.getUsername(), foundPlayer.getUsername());
        assertEquals(player.getPassword(), foundPlayer.getPassword());
        assertEquals(player.getBalance(), foundPlayer.getBalance());
    }
    @Test
    void testFindNonExistentPlayerByUsername() {
        Optional<Player> notFoundPlayerOpt = repository.findPlayerByUsername("nonExistentUsername");
        assertFalse(notFoundPlayerOpt.isPresent());
    }

    @Test
    void testUpdatePlayer() {
        Player player = new Player();
        player.setUsername("testUsername");
        player.setPassword("testPassword");
        player.setBalance(new BigDecimal("300.00"));

        repository.addPlayer(player);

        Optional<Player> initialPlayerOpt = repository.findPlayerById(player.getId());
        assertTrue(initialPlayerOpt.isPresent());
        assertEquals(new BigDecimal("300.00"), initialPlayerOpt.get().getBalance());

        player.setBalance(new BigDecimal("400.00"));
        repository.updatePlayer(player);

        Optional<Player> updatedPlayerOpt = repository.findPlayerById(player.getId());
        assertTrue(updatedPlayerOpt.isPresent());
        assertEquals(new BigDecimal("400.00"), updatedPlayerOpt.get().getBalance());
    }
    @Test
    void testFindPlayerById() {
        Player player = new Player();
        player.setId(1L);
        player.setUsername("testUser");
        player.setPassword("testPassword");
        player.setBalance(new BigDecimal("100.00"));

        repository.addPlayer(player);

        Optional<Player> foundPlayerOpt = repository.findPlayerById(player.getId());
        assertTrue(foundPlayerOpt.isPresent());

        Player foundPlayer = foundPlayerOpt.get();
        assertEquals(player.getUsername(), foundPlayer.getUsername());
        assertEquals(player.getPassword(), foundPlayer.getPassword());
        assertEquals(player.getBalance(), foundPlayer.getBalance());
    }
}
