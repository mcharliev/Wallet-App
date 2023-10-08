package ru.zenclass.ylab.service;

import org.junit.Before;
import org.junit.Test;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.repository.PlayerRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PlayerServiceTest {

    private PlayerService playerService;
    private PlayerRepository playerRepository;

    @Before
    public void setUp() {
        playerRepository = mock(PlayerRepository.class);
        playerService = new PlayerService(playerRepository);
    }

    @Test
    public void testAddPlayer() {
        Player player = new Player();
        player.setUsername("testuser");
        player.setPassword("password");

        playerService.addPlayer(player);

        //проверяю, что метод addPlayer объекта playerRepository был вызван ровно 1 раз с аргументом player
        verify(playerRepository, times(1)).addPlayer(player);
    }

    @Test
    public void testGetAllPlayers() {
        List<Player> players = new ArrayList<>();

        Player player1 = new Player();
        player1.setId("1");
        player1.setUsername("user1");
        player1.setPassword("password1");
        player1.setBalance(BigDecimal.ZERO);

        Player player2 = new Player();
        player2.setId("2");
        player2.setUsername("user2");
        player2.setPassword("password2");
        player2.setBalance(BigDecimal.ZERO);

        players.add(player1);
        players.add(player2);


        when(playerRepository.getAllPlayers()).thenReturn(players);

        List<Player> retrievedPlayers = playerService.getAllPlayers();

        assertEquals(2, retrievedPlayers.size());
        assertEquals("user1", retrievedPlayers.get(0).getUsername());
        assertEquals("user2", retrievedPlayers.get(1).getUsername());
    }

    @Test
    public void testFindPlayerById() {
        Player player = new Player();
        player.setId("1");
        player.setUsername("user1");
        player.setPassword("password1");
        player.setBalance(BigDecimal.ZERO);

        when(playerRepository.findPlayerById("1")).thenReturn(player);

        Player retrievedPlayer = playerService.findPlayerById("1");

        assertEquals("user1", retrievedPlayer.getUsername());
    }

    @Test
    public void testUpdatePlayer() {
        List<Player> players = new ArrayList<>();

        Player player = new Player();
        player.setId("1");
        player.setUsername("user1");
        player.setPassword("password1");
        player.setBalance(BigDecimal.ZERO);
        players.add(player);

        when(playerRepository.getAllPlayers()).thenReturn(players);

        Player updatedPlayer = new Player();
        updatedPlayer.setId("1");
        updatedPlayer.setUsername("updatedUser");
        updatedPlayer.setPassword("updatedPassword");
        updatedPlayer.setBalance(BigDecimal.TEN);

        playerService.updatePlayer(updatedPlayer);

        assertEquals("updatedUser", players.get(0).getUsername());
        assertEquals("updatedPassword", players.get(0).getPassword());
        assertEquals(BigDecimal.TEN, players.get(0).getBalance());
    }

}






