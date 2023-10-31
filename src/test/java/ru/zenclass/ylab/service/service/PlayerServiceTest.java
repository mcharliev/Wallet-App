package ru.zenclass.ylab.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import ru.zenclass.ylab.configuration.AppConfig;
import ru.zenclass.ylab.exception.AuthenticationException;
import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.model.dto.LoginResponseDTO;
import ru.zenclass.ylab.model.dto.PlayerBalanceDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.service.impl.PlayerServiceImpl;
import ru.zenclass.ylab.util.DTOValidator;
import ru.zenclass.ylab.util.JwtUtil;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = AppConfig.class)
 class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private DTOValidator<RegisterPlayerDTO> registerPlayerValidator;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindPlayerById_PlayerExists() {
        Long id = 1L;
        Player mockPlayer = new Player();
        when(playerRepository.findPlayerById(id)).thenReturn(Optional.of(mockPlayer));
        Player result = playerService.findPlayerById(id);
        assertEquals(mockPlayer, result);
        verify(playerRepository).findPlayerById(id);
    }

    @Test
    public void testFindPlayerById_PlayerNotFound() {
        Long id = 1L;
        when(playerRepository.findPlayerById(id)).thenReturn(Optional.empty());
        assertThrows(PlayerNotFoundException.class, () -> playerService.findPlayerById(id));
        verify(playerRepository).findPlayerById(id);
    }

    @Test
    public void testAuthenticateAndGenerateToken_Success() {
        String username = "player1";
        String password = "pass123";
        RegisterPlayerDTO registerPlayerDTO = new RegisterPlayerDTO();
        registerPlayerDTO.setUsername(username);
        registerPlayerDTO.setPassword(password);
        Player mockPlayer = new Player(username, password);
        String token = "someToken";

        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.of(mockPlayer));
        when(jwtUtil.generateToken(username)).thenReturn(token);
        LoginResponseDTO result = playerService.authenticateAndGenerateToken(registerPlayerDTO);

        assertEquals(username, result.getPlayer().getUsername());
        assertEquals(token, result.getToken());
    }
    @Test
    public void testUpdatePlayer() {
        Player updatedPlayer = new Player("player2", "newpass");
        doNothing().when(playerRepository).updatePlayer(updatedPlayer);

        playerService.updatePlayer(updatedPlayer);

        verify(playerRepository).updatePlayer(updatedPlayer);
    }

    @Test
    public void testRegisterNewPlayer_NewPlayer() {
        RegisterPlayerDTO registerPlayerDTO = new RegisterPlayerDTO();
        registerPlayerDTO.setUsername("newplayer");
        registerPlayerDTO.setPassword("pass123");
        when(playerRepository.findPlayerByUsername("newplayer")).thenReturn(Optional.empty());

        playerService.registerNewPlayer(registerPlayerDTO);

        verify(playerRepository).findPlayerByUsername("newplayer");
        verify(playerRepository).addPlayer(any(Player.class));
    }

    @Test
    public void testFindPlayerByUsername_PlayerExists() {
        String username = "player1";
        Player mockPlayer = new Player(username, "pass123");
        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.of(mockPlayer));

        Optional<Player> result = playerService.findPlayerByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(mockPlayer, result.get());
    }

    @Test
    public void testFindPlayerByUsername_PlayerNotFound() {
        String username = "player1";
        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.empty());

        Optional<Player> result = playerService.findPlayerByUsername(username);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetPlayerBalanceInfo() {
        Player player = new Player("player1", "pass123");
        player.setBalance(BigDecimal.valueOf(100.0));

        PlayerBalanceDTO result = playerService.getPlayerBalanceInfo(player);

        assertEquals(player.getUsername(), result.getUsername());
        assertEquals(player.getBalance(), result.getBalance());
    }

    @Test
    public void testValidateTokenAndGetPlayer_ValidToken() {
        String token = "Bearer validToken";
        String username = "player1";

        when(jwtUtil.extractUsername("validToken")).thenReturn(username);
        when(jwtUtil.validateToken("validToken", username)).thenReturn(true);
        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.of(new Player(username, "pass123")));

        Optional<Player> result = playerService.validateTokenAndGetPlayer(token);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }

    @Test
    public void testValidateTokenAndGetPlayer_NoBearer() {
        String token = "invalidToken";

        Optional<Player> result = playerService.validateTokenAndGetPlayer(token);

        assertFalse(result.isPresent());
    }

    @Test
    public void testAuthenticateAndGenerateToken_Failure() {
        String username = "player1";
        String password = "wrongpass";
        RegisterPlayerDTO registerPlayerDTO = new RegisterPlayerDTO();
        registerPlayerDTO.setUsername(username);
        registerPlayerDTO.setPassword(password);
        Player mockPlayer = new Player(username, "correctpass");

        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.of(mockPlayer));

        assertThrows(AuthenticationException.class, () -> playerService.authenticateAndGenerateToken(registerPlayerDTO));

        verify(jwtUtil, never()).generateToken(anyString());
    }
}