//package ru.zenclass.ylab.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import ru.zenclass.ylab.exception.PlayerAlreadyExistException;
//import ru.zenclass.ylab.model.Player;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.util.Collections;
//import java.util.Scanner;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//
//public class AdminServiceTest {
//
//    @Mock
//    private PlayerService mockPlayerService;
//
//    @InjectMocks
//    private AdminService adminService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//
//    @Test
//    public void testLoginSuccess() {
//        String input = "testuser\npassword\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        Scanner scanner = new Scanner(in);
//
//        // Создаем игрока с теми же данными, что мы ввели выше
//        Player testPlayer = new Player();
//        testPlayer.setUsername("testuser");
//        testPlayer.setPassword("password");
//        when(mockPlayerService.getAllPlayers()).thenReturn(Collections.singletonList(testPlayer));
//
//        // Вызываем метод авторизации с имитированным вводом
//        Player loggedInPlayer = adminService.login(null,scanner);
//
//        // Проверяем, что метод authenticatePlayer был вызван и вернул ожидаемого игрока
//        verify(mockPlayerService, times(1)).getAllPlayers();
//        assertEquals(testPlayer, loggedInPlayer);
//    }
//
//    @Test
//    public void testLoginFailure() {
//        String input = "nonexistentuser\nwrongpassword\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        Scanner scanner = new Scanner(in);
//
//        // Мокируем getAllPlayers, чтобы возвращать пустой список, симулируя отсутствие существующих игроков
//        when(mockPlayerService.getAllPlayers()).thenReturn(Collections.emptyList());
//
//        // Вызываем метод авторизации с имитированным вводом
//        Player loggedInPlayer = adminService.login(null,scanner);
//
//        // Проверяем, что метод authenticatePlayer был вызван и вернул null (неправильные учетные данные)
//        verify(mockPlayerService, times(1)).getAllPlayers();
//        assertEquals(null, loggedInPlayer);
//    }
//
//    @Test
//    public void testRegisterPlayerSuccess() {
//        String input = "testuser\npassword\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        Scanner scanner = new Scanner(in);
//
//        // Вызываем метод getAllPlayers, возвращая пустой список
//        when(mockPlayerService.getAllPlayers()).thenReturn(Collections.emptyList());
//
//        // Вызываем метод регистрации с имитированным вводом
//        adminService.registerPlayer(scanner);
//
//        // Проверяем, что метод addPlayer был вызван один раз
//        verify(mockPlayerService, times(1)).addPlayer(any(Player.class));
//    }
//
//    @Test
//    public void testRegisterPlayerAlreadyExists() {
//        Player existingPlayer = new Player();
//        existingPlayer.setUsername("testuser");
//        when(mockPlayerService.getAllPlayers()).thenReturn(Collections.singletonList(existingPlayer));
//
//        String input = "testuser\npassword\n";
//        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
//        Scanner scanner = new Scanner(in);
//
//        // Проверяем, что выбрасывается исключение PlayerAlreadyExistException
//        assertThrows(PlayerAlreadyExistException.class, () -> {
//            adminService.registerPlayer(scanner);
//        });
//
//        // Убеждаемся, что метод addPlayer не был вызван
//        verify(mockPlayerService, never()).addPlayer(any(Player.class));
//    }
//
//}
