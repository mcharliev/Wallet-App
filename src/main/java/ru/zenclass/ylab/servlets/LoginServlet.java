package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.mapper.PlayerMapper;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.PlayerRepositoryImpl;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.PlayerServiceImpl;
import ru.zenclass.ylab.util.JwtUtil;

import java.io.IOException;
import java.util.Optional;

/**
 * Сервлет, предназначенный для авторизации пользователей.
 * <p>
 * После успешной авторизации пользователь получает JSON-ответ с сообщением об успехе, данными пользователя
 * и JWT-токеном. В случае ошибки авторизации возвращает сообщение об ошибке.
 * </p>
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/players/login"})
public class LoginServlet extends BasicRegLogServlet {

    private PlayerService playerService;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Инициализирует сервлет, создавая и настраивая необходимые службы
     */
    @Override
    public void init() {
        super.init();
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
        this.playerService = new PlayerServiceImpl(playerRepository);
    }

    /**
     * Обрабатывает POST-запрос для авторизации пользователя.
     *
     * @param req  запрос от клиента
     * @param resp ответ сервера
     * @throws IOException в случае ошибок ввода-вывода
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RegisterPlayerDTO registerPlayerDTO = getAndValidatePlayerDTO(req, resp);
        if (registerPlayerDTO == null) {
            return;
        }
        Player playerEntity = PlayerMapper.INSTANCE.toPlayerEntity(registerPlayerDTO);
        Optional<Player> playerOpt = playerService.login(playerEntity.getUsername(), playerEntity.getPassword());
        if (playerOpt.isPresent()) {
            Player registeredPlayer = playerOpt.get();
            PlayerDTO playerDTO = PlayerMapper.INSTANCE.toDTO(registeredPlayer);
            JwtUtil jwtUtil = new JwtUtil();
            String token = jwtUtil.generateToken(registeredPlayer.getUsername());
            String jsonResponse = String.format(
                    "{ \"message\": \"Пользователь '%s' совершил успешный вход\", \"player\": %s, \"token\": \"%s\" }",
                    playerDTO.getUsername(),
                    mapper.writeValueAsString(playerDTO),
                    token);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(jsonResponse);
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Неверый логин или пароль, попробуйте ввести данные снова\"}");
        }
    }
}