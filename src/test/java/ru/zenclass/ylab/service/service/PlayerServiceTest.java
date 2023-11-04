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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = AppConfig.class)
class PlayerServiceTest {
//
//    @Mock
//    private PlayerRepository playerRepository;
//
//    @Mock
//    private DTOValidator<RegisterPlayerDTO> registerPlayerValidator;
//
//    @Mock
//    private JwtUtil jwtUtil;
//
//    @InjectMocks
//    private PlayerServiceImpl playerService;
//
//    private Long id;
//    private Player mockPlayer;
//    private RegisterPlayerDTO registerPlayerDTO;
//    private String token;
//    private String username;
//    private String password;
//    private Player updatedPlayer;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        id = 1L;
//        username = "player1";
//        password = "pass123";
//        token = "someToken";
//        mockPlayer = new Player(username, password);
//        updatedPlayer = new Player("player2", "newpass");
//
//        registerPlayerDTO = new RegisterPlayerDTO();
//        registerPlayerDTO.setUsername(username);
//        registerPlayerDTO.setPassword(password);
//    }
//
//    @Test
//    public void testFindPlayerById_PlayerExists() {
//        when(playerRepository.findPlayerById(id)).thenReturn(Optional.of(mockPlayer));
//        Player result = playerService.findPlayerById(id);
//        assertThat(result).isEqualTo(mockPlayer);
//        verify(playerRepository).findPlayerById(id);
//    }
//
//    @Test
//    public void testFindPlayerById_PlayerNotFound() {
//        when(playerRepository.findPlayerById(id)).thenReturn(Optional.empty());
//        assertThatExceptionOfType(PlayerNotFoundException.class)
//                .isThrownBy(() -> playerService.findPlayerById(id));
//        verify(playerRepository).findPlayerById(id);
//    }
//
//    @Test
//    public void testAuthenticateAndGenerateToken_Success() {
//        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.of(mockPlayer));
//        when(jwtUtil.generateToken(username)).thenReturn(token);
//        LoginResponseDTO result = playerService.authenticateAndGenerateToken(registerPlayerDTO);
//
//        assertThat(result.getPlayer().getUsername()).isEqualTo(username);
//        assertThat(result.getToken()).isEqualTo(token);
//    }
//
//    @Test
//    public void testUpdatePlayer() {
//        doNothing().when(playerRepository).updatePlayer(updatedPlayer);
//
//        playerService.updatePlayer(updatedPlayer);
//
//        verify(playerRepository).updatePlayer(updatedPlayer);
//    }
//
//    @Test
//    public void testRegisterNewPlayer_NewPlayer() {
//        registerPlayerDTO.setUsername("newplayer");
//
//        when(playerRepository.findPlayerByUsername("newplayer")).thenReturn(Optional.empty());
//
//        playerService.registerNewPlayer(registerPlayerDTO);
//
//        verify(playerRepository).findPlayerByUsername("newplayer");
//        verify(playerRepository).addPlayer(any(Player.class));
//    }
//
//    @Test
//    public void testFindPlayerByUsername_PlayerExists() {
//        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.of(mockPlayer));
//
//        Optional<Player> result = playerService.findPlayerByUsername(username);
//
//        assertThat(result).isPresent();
//        assertThat(result.get()).isEqualTo(mockPlayer);
//    }
//
//    @Test
//    public void testFindPlayerByUsername_PlayerNotFound() {
//        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.empty());
//
//        Optional<Player> result = playerService.findPlayerByUsername(username);
//
//        assertThat(result).isNotPresent();
//    }
//
//    @Test
//    public void testGetPlayerBalanceInfo() {
//        mockPlayer.setBalance(BigDecimal.valueOf(100.0));
//
//        PlayerBalanceDTO result = playerService.getPlayerBalanceInfo(mockPlayer);
//
//        assertThat(result.getUsername()).isEqualTo(mockPlayer.getUsername());
//        assertThat(result.getBalance()).isEqualByComparingTo(mockPlayer.getBalance());
//    }
//
//    @Test
//    public void testValidateTokenAndGetPlayer_ValidToken() {
//        String validToken = "Bearer validToken";
//
//        when(jwtUtil.extractUsername("validToken")).thenReturn(username);
//        when(jwtUtil.validateToken("validToken", username)).thenReturn(true);
//        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.of(mockPlayer));
//
//        Optional<Player> result = playerService.validateTokenAndGetPlayer(validToken);
//
//        assertThat(result).isPresent();
//        assertThat(result.get().getUsername()).isEqualTo(username);
//    }
//
//    @Test
//    public void testValidateTokenAndGetPlayer_NoBearer() {
//        String invalidToken = "invalidToken";
//
//        Optional<Player> result = playerService.validateTokenAndGetPlayer(invalidToken);
//
//        assertThat(result).isNotPresent();
//    }
//
//    @Test
//    public void testAuthenticateAndGenerateToken_Failure() {
//        String wrongPassword = "wrongpass";
//        registerPlayerDTO.setPassword(wrongPassword);
//        Player wrongPasswordPlayer = new Player(username, "correctpass");
//
//        when(playerRepository.findPlayerByUsername(username)).thenReturn(Optional.of(wrongPasswordPlayer));
//
//        assertThatExceptionOfType(AuthenticationException.class)
//                .isThrownBy(() -> playerService.authenticateAndGenerateToken(registerPlayerDTO));
//
//        verify(jwtUtil, never()).generateToken(anyString());
//    }
}