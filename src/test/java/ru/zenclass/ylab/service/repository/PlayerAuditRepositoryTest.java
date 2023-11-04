package ru.zenclass.ylab.service.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.zenclass.ylab.model.entity.PlayerAudit;
import ru.zenclass.ylab.model.enums.PlayerActionType;
import ru.zenclass.ylab.repository.PlayerAuditRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Testcontainers
public class PlayerAuditRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");


    @Autowired
    private PlayerAuditRepository playerAuditRepository;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("database.url", postgres::getJdbcUrl);
        registry.add("database.password", postgres::getPassword);
        registry.add("database.username", postgres::getUsername);
    }

    @Test
    void testAddPlayerAudit() {
        PlayerAudit playerAudit = new PlayerAudit();
        playerAudit.setPlayerId(1L);
        playerAudit.setActionType(PlayerActionType.AUTHENTICATION.getAction());
        playerAudit.setActionDate(LocalDateTime.now());
        playerAudit.setDetails("Игрок совершил успешный вход");

        playerAuditRepository.addPlayerAudit(playerAudit);

        List<PlayerAudit> audits = playerAuditRepository.findAuditsByPlayerId(1L);
        assertThat(audits).isNotEmpty();
        assertThat(audits.get(0).getActionType()).isEqualTo("AUTHENTICATION");
        assertThat(audits.get(0).getDetails()).isEqualTo("Игрок совершил успешный вход");
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
        assertThat(audits).hasSize(3);
    }

    @Test
    void testFindAuditsByPlayerId_NoAudits() {
        Long playerId = 3L;
        List<PlayerAudit> audits = playerAuditRepository.findAuditsByPlayerId(playerId);
        assertThat(audits).isEmpty();
    }
}
