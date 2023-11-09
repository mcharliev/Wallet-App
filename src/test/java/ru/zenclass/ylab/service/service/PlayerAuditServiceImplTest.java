package ru.zenclass.ylab.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zenclass.ylab.model.entity.PlayerAudit;
import ru.zenclass.ylab.model.enums.PlayerActionType;
import ru.zenclass.ylab.repository.PlayerAuditRepository;
import ru.zenclass.ylab.service.impl.PlayerAuditServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerAuditServiceImplTest {

    @Mock
    private PlayerAuditRepository playerAuditRepository;

    @InjectMocks
    private PlayerAuditServiceImpl playerAuditService;

    @Test
    public void testLogPlayerAction() {
        Long playerId = 1L;
        String actionType = PlayerActionType.AUTHENTICATION.getAction();
        String details = "Игрок совершил успешный вход";

        playerAuditService.logPlayerAction(playerId, actionType, details);

        ArgumentCaptor<PlayerAudit> auditCaptor = ArgumentCaptor.forClass(PlayerAudit.class);
        verify(playerAuditRepository).addPlayerAudit(auditCaptor.capture());

        PlayerAudit capturedAudit = auditCaptor.getValue();
        assertThat(capturedAudit.getPlayerId()).isEqualTo(playerId);
        assertThat(capturedAudit.getActionType()).isEqualTo(actionType);
        assertThat(capturedAudit.getDetails()).isEqualTo(details);
        assertThat(capturedAudit.getActionDate()).isNotNull();
    }

    @Test
    public void testGetAuditForPlayer() {
        Long playerId = 1L;
        List<PlayerAudit> expectedAudits = Arrays.asList(new PlayerAudit(), new PlayerAudit());

        when(playerAuditRepository.findAuditsByPlayerId(playerId)).thenReturn(expectedAudits);

        List<PlayerAudit> actualAudits = playerAuditService.getAuditForPlayer(playerId);

        assertThat(actualAudits).isEqualTo(expectedAudits);
        verify(playerAuditRepository).findAuditsByPlayerId(playerId);
    }
}

