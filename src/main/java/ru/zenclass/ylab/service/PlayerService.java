package ru.zenclass.ylab.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.exception.AuthorisationException;
import ru.zenclass.ylab.exception.PlayerAlreadyExistException;
import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.model.Player;
import ru.zenclass.ylab.repository.PlayerRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

/**
 * Сервис для управления данными игрока.
 * Этот сервис предоставляет методы для выполнения основных операций, таких как поиск, обновление, регистрация и вход.
 */
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    /**
     * Поиск игрока по его идентификатору.
     *
     * @param id идентификатор игрока.
     * @return найденный игрок.
     * @throws PlayerNotFoundException если игрок с данным идентификатором не найден.
     */
    public Player findPlayerById(Long id) {
        return playerRepository.findPlayerById(id).orElseThrow(PlayerNotFoundException::new);
    }

    /**
     * Обновление данных игрока.
     *
     * @param updatedPlayer объект игрока с обновленными данными.
     */
    public void updatePlayer(Player updatedPlayer) {
        playerRepository.updatePlayer(updatedPlayer);
    }

    /**
     * Регистрация нового игрока.
     *
     * @param scanner сканер для считывания ввода данных игрока.
     */
    public void registerPlayer(Scanner scanner) {
        // Считываем имя пользователя.
        System.out.print("Введите имя: ");
        String username = scanner.nextLine();

        // Считываем пароль пользователя.
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        try {
            // Проверяем существует ли игрок с таким именем в репозитории.
            Optional<Player> optPlayer = playerRepository.findPlayerByUsername(username);
            if (optPlayer.isEmpty()) {
                // Создаем и инициализируем нового игрока.
                Player player = new Player();
                player.setUsername(username);
                player.setPassword(password);
                player.setBalance(BigDecimal.ZERO);

                // Сохраняем нового игрока в БД.
                playerRepository.addPlayer(player);

                log.info("Пользователь " + player.getUsername() + " успешно зарегистрировался");
                System.out.println("------------------------------------------------------------------");
                System.out.println("Регистрация успешна. Теперь вы можете войти.");
                System.out.println("------------------------------------------------------------------");
            } else {
                // Выбрасываем исключение, если игрок с таким именем уже существует.
                throw new PlayerAlreadyExistException();
            }
            // Сразу ловлю исключение и вывожу сообщение об ошибке, чтобы приложение не упало
        } catch (PlayerAlreadyExistException e) {
            log.error("Ошибка регистрации пользователя");
            System.out.println("Пользователь с таким именем уже существует. Пожалуйста, выберите другое имя.");
        }
    }

    /**
     * Процесс входа игрока в систему.
     *
     * @param loggedInPlayer текущий авторизованный игрок.
     * @param scanner        сканер для считывания ввода данных игрока.
     * @return объект авторизованного игрока.
     */
    public Player login(Player loggedInPlayer, Scanner scanner) {
        // Считываем имя пользователя для входа.
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();

        // Считываем пароль пользователя для входа.
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        try {
            // Аутентифицируем пользователя с помощью введенных данных.
            Player player = authenticatePlayer(username, password);

            if (player != null) {
                // Если аутентификация прошла успешно, обновляем текущего авторизованного пользователя.
                loggedInPlayer = player;

                System.out.println("Авторизация успешна. Добро пожаловать, " + loggedInPlayer.getUsername() + "!");
                log.info("Пользователь " + loggedInPlayer.getUsername() + " прошел авторизацию");
            } else {
                // Выбрасываем исключение, если аутентификация не удалась.
                throw new AuthorisationException();
            }
            // Сразу ловлю исключение и вывожу сообщение об ошибке, чтобы приложение не упало
        } catch (AuthorisationException e) {
            log.error("Ошибка авторизации пользователя");
            System.out.println("Ошибка авторизации. Пожалуйста, проверьте введенные данные.");
        }

        return loggedInPlayer;
    }

    /**
     * Аутентификация игрока на основе имени пользователя и пароля.
     *
     * @param username имя пользователя.
     * @param password пароль пользователя.
     * @return объект игрока, если аутентификация прошла успешно, иначе null.
     */
    private Player authenticatePlayer(String username, String password) {
        // Поиск игрока в репозитории по имени пользователя.
        Optional<Player> optPlayer = playerRepository.findPlayerByUsername(username);

        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();

            // Проверка соответствия имени пользователя и пароля.
            if (player.getUsername().equals(username) && player.getPassword().equals(password)) {
                return player; // Возвращаем игрока, если данные совпадают.
            }
        }
        // Возвращаю null потому что в классе WalletAppService хранится аутентифицированный
        // пользователь loggedInPlayer, с соответствующими проверками на null, если он null то будет
        // отображаться соответствующее консольное меню
        return null;
    }
}
