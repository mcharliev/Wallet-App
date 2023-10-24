package ru.zenclass.ylab.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zenclass.ylab.exception.AuthenticationException;
import ru.zenclass.ylab.exception.ConflictException;
import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.model.dto.LoginResponseDTO;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.service.PlayerServiceImpl;
import ru.zenclass.ylab.util.JwtUtil;
import ru.zenclass.ylab.validator.RegisterPlayerValidator;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private RegisterPlayerValidator registerPlayerValidator;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PlayerServiceImpl playerService;
    @Test
    void testFindPlayerById_existingId() {
        Player player = new Player();
        player.setUsername("testUser");
        when(playerRepository.findPlayerById(1L)).thenReturn(Optional.of(player));
        Player result = playerService.findPlayerById(1L);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testFindPlayerById_nonExistingId() {
        when(playerRepository.findPlayerById(1L)).thenReturn(Optional.empty());
        assertThrows(PlayerNotFoundException.class, () -> {
            playerService.findPlayerById(1L);
        });
    }

    @Test
    void testRegisterNewPlayer_alreadyExists() {
        RegisterPlayerDTO registerPlayerDTO = new RegisterPlayerDTO();
        registerPlayerDTO.setUsername("testUser");
        registerPlayerDTO.setPassword("testPass");
        Player player = new Player("testUser", "testPass");
        when(playerRepository.findPlayerByUsername("testUser")).thenReturn(Optional.of(player));
        assertThrows(ConflictException.class, () -> {
            playerService.registerNewPlayer(registerPlayerDTO);
        });
    }

    @Test
    void testRegisterNewPlayer_successful() {
        RegisterPlayerDTO registerPlayerDTO = new RegisterPlayerDTO();
        registerPlayerDTO.setUsername("newUser");
        registerPlayerDTO.setPassword("newPass");
        when(playerRepository.findPlayerByUsername("newUser")).thenReturn(Optional.empty());
        PlayerDTO result = playerService.registerNewPlayer(registerPlayerDTO);
        assertEquals("newUser", result.getUsername());
    }

    @Test
    void testGetPlayerBalanceInfo_nullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> {
            playerService.getPlayerBalanceInfo(null);
        });
    }

    @Test
    void testGetPlayerBalanceInfo_validPlayer() {
        Player player = new Player("testUser", "testPass");
        player.setBalance(new BigDecimal("100.50"));
        String result = playerService.getPlayerBalanceInfo(player);
        assertEquals("{\"username\": \"testUser\", \"balance\": \"100.50\"}", result);
    }
    @Test
    void testAuthenticateAndGenerateToken_successful() {
        String username = "testUser";
        String password = "testPass";
        Player player = new Player(username, password);
        String token = "testToken";
        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.of(player));
        when(jwtUtil.generateToken(username)).thenReturn(token);
        LoginResponseDTO result = playerService.authenticateAndGenerateToken(username, password);
        assertEquals(username, result.getPlayerDTO().getUsername());
        assertEquals(token, result.getToken());
    }

    @Test
    void testAuthenticateAndGenerateToken_invalidCredentials() {
        String username = "testUser";
        String password = "wrongPass";
        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class, () -> {
            playerService.authenticateAndGenerateToken(username, password);
        });
    }

    @Test
    void testUpdatePlayer() {
        Player updatedPlayer = new Player("testUser", "newTestPass");
        assertDoesNotThrow(() -> playerService.updatePlayer(updatedPlayer));
        verify(playerRepository).updatePlayer(updatedPlayer);  // Проверяем, что метод обновления был вызван
    }
}
