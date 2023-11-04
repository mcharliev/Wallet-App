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
import ru.zenclass.ylab.model.enums.PlayerActionType;
import ru.zenclass.ylab.repository.PlayerAuditRepository;
import ru.zenclass.ylab.service.config.TestDataSourceConfig;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringJUnitConfig(classes = {AppConfig.class, TestDataSourceConfig.class})
@Testcontainers
@WebAppConfiguration
public class PlayerAuditRepositoryTest {

//    @Container
//    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    private PlayerAuditRepository playerAuditRepository;
//
//    @DynamicPropertySource
//    static void postgresqlProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.password", postgres::getPassword);
//        registry.add("spring.datasource.username", postgres::getUsername);
//    }
//
//    @BeforeEach
//    void setUp() {
//        SpringLiquibase liquibase = applicationContext.getBean(SpringLiquibase.class);
//        try {
//            liquibase.afterPropertiesSet();
//        } catch (Exception e) {
//            throw new RuntimeException("Ошибка при запуске миграций Liquibase", e);
//        }
//    }
//
//    @Test
//    void testAddPlayerAudit() {
//        PlayerAudit playerAudit = new PlayerAudit();
//        playerAudit.setPlayerId(1L);
//        playerAudit.setActionType(PlayerActionType.AUTHENTICATION.getAction());
//        playerAudit.setActionDate(LocalDateTime.now());
//        playerAudit.setDetails("Игрок совершил успешный вход");
//
//        playerAuditRepository.addPlayerAudit(playerAudit);
//
//        List<PlayerAudit> audits = playerAuditRepository.findAuditsByPlayerId(1L);
//        assertThat(audits).isNotEmpty();
//        assertThat(audits.get(0).getActionType()).isEqualTo("AUTHENTICATION");
//        assertThat(audits.get(0).getDetails()).isEqualTo("Игрок совершил успешный вход");
//    }
//
//    @Test
//    void testFindAuditsByPlayerId() {
//        Long playerId = 2L;
//
//        for (int i = 0; i < 3; i++) {
//            PlayerAudit playerAudit = new PlayerAudit();
//            playerAudit.setPlayerId(playerId);
//            playerAudit.setActionType("ACTION_TYPE_" + i);
//            playerAudit.setActionDate(LocalDateTime.now());
//            playerAudit.setDetails("Details for action " + i);
//            playerAuditRepository.addPlayerAudit(playerAudit);
//        }
//
//        List<PlayerAudit> audits = playerAuditRepository.findAuditsByPlayerId(playerId);
//        assertThat(audits).hasSize(3);
//    }
//
//    @Test
//    void testFindAuditsByPlayerId_NoAudits() {
//        Long playerId = 3L;
//        List<PlayerAudit> audits = playerAuditRepository.findAuditsByPlayerId(playerId);
//        assertThat(audits).isEmpty();
//    }
}
