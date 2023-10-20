package ru.zenclass.ylab.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.PlayerServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class PlayerServiceTest {

    private PlayerService playerService;
    @Mock
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        playerService = new PlayerServiceImpl(playerRepository);
    }

    @Test
    void testFindPlayerById() {
        Player player = new Player();
        player.setId(1L);
        player.setUsername("testUser");
        player.setPassword("testPassword");
        player.setBalance(new BigDecimal("100.00"));

        Mockito.when(playerRepository.findPlayerById(player.getId())).thenReturn(Optional.of(player));

        Player foundPlayer = playerService.findPlayerById(player.getId());
        assertEquals(player.getUsername(), foundPlayer.getUsername());
    }

    @Test
    void testLogin() {
        Player player = new Player();
        player.setUsername("loginUser");
        player.setPassword("loginPassword");
        player.setBalance(new BigDecimal("100.00"));

        Mockito.when(playerRepository.findPlayerByUsername(player.getUsername())).thenReturn(Optional.of(player));

        Optional<Player> loggedInPlayerOpt = playerService.login("loginUser", "loginPassword");

        assertTrue(loggedInPlayerOpt.isPresent());
        Player loggedInPlayer = loggedInPlayerOpt.get();
        assertEquals(player.getUsername(), loggedInPlayer.getUsername());
        assertEquals(player.getPassword(), loggedInPlayer.getPassword());
        assertEquals(player.getBalance(), loggedInPlayer.getBalance());
    }

    @Test
    void testRegisterPlayerWithExistingUsername() {
        Player existingPlayer = new Player();
        existingPlayer.setUsername("existingUser");
        existingPlayer.setPassword("existingPassword");

        Mockito.when(playerRepository.findPlayerByUsername(existingPlayer.getUsername())).thenReturn(Optional.of(existingPlayer));

        // Проверяем, что при попытке регистрации пользователя с существующим именем выводится нужное сообщение
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Optional<Player> registeredPlayerOpt = playerService.registerPlayer("existingUser", "anotherPassword");

        assertFalse(registeredPlayerOpt.isPresent());
        String expectedOutput = "Пользователь с таким именем уже существует. Пожалуйста, выберите другое имя.";
        assertTrue(outContent.toString().contains(expectedOutput));
    }
    @Test
    void testUnsuccessfulLogin() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Optional<Player> loggedInPlayerOpt = playerService.login("nonexistentUser", "wrongPassword");

        assertFalse(loggedInPlayerOpt.isPresent());

        String expectedOutput = "Ошибка авторизации пользователя с именем nonexistentUser";
        assertTrue(outContent.toString().contains(expectedOutput));
    }

    @Test
    void testUpdatePlayer() {
        Player player = new Player();
        player.setId(1L);
        player.setUsername("updateUser");
        player.setPassword("updatePassword");
        player.setBalance(new BigDecimal("100.00"));

        Mockito.when(playerRepository.findPlayerById(player.getId())).thenReturn(Optional.of(player));
        Mockito.doNothing().when(playerRepository).updatePlayer(Mockito.any(Player.class));

        player.setBalance(new BigDecimal("150.00"));
        playerService.updatePlayer(player);

        Optional<Player> updatedPlayerOpt = playerRepository.findPlayerById(player.getId());
        assertTrue(updatedPlayerOpt.isPresent());
        assertEquals(new BigDecimal("150.00"), updatedPlayerOpt.get().getBalance());
    }
}
