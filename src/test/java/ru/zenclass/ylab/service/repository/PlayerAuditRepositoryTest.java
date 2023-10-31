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
import ru.zenclass.ylab.model.entity.PlayerAudit;
import ru.zenclass.ylab.repository.PlayerAuditRepository;
import ru.zenclass.ylab.service.config.TestDataSourceConfig;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {AppConfig.class, TestDataSourceConfig.class})
@Testcontainers
@WebAppConfiguration
public class PlayerAuditRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlayerAuditRepository playerAuditRepository;

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
    void testAddPlayerAudit() {
        PlayerAudit playerAudit = new PlayerAudit();
        playerAudit.setPlayerId(1L);
        playerAudit.setActionType("LOGIN");
        playerAudit.setActionDate(LocalDateTime.now());
        playerAudit.setDetails("User logged in successfully");

        playerAuditRepository.addPlayerAudit(playerAudit);

        List<PlayerAudit> audits = playerAuditRepository.findAuditsByPlayerId(1L);
        assertFalse(audits.isEmpty());
        assertEquals("LOGIN", audits.get(0).getActionType());
        assertEquals("User logged in successfully", audits.get(0).getDetails());
    }

    @Test
    void testFindAuditsByPlayerId() {
        Long playerId = 2L;

        for (int i = 0; i < 3; i++) {
            PlayerAudit playerAudit = new PlayerAudit();
            playerAudit.setPlayerId(playerId);
            playerAudit.setActionType("ACTION_TYPE_" + i);
            playerAudit.setActionDate(LocalDateTime.now());
            playerAudit.setDetails("Details for action " + i);
            playerAuditRepository.addPlayerAudit(playerAudit);
        }

        List<PlayerAudit> audits = playerAuditRepository.findAuditsByPlayerId(playerId);
        assertEquals(3, audits.size());

        // Проверяем, что все добавленные записи присутствуют, независимо от порядка
        List<String> expectedActionTypes = IntStream.range(0, 3)
                .mapToObj(i -> "ACTION_TYPE_" + i)
                .collect(Collectors.toList());

        List<String> actualActionTypes = audits.stream()
                .map(PlayerAudit::getActionType)
                .collect(Collectors.toList());

        assertTrue(actualActionTypes.containsAll(expectedActionTypes));
    }

    @Test
    void testFindAuditsByPlayerId_NoAudits() {
        Long playerId = 3L;
        List<PlayerAudit> audits = playerAuditRepository.findAuditsByPlayerId(playerId);
        assertTrue(audits.isEmpty());
    }
}
