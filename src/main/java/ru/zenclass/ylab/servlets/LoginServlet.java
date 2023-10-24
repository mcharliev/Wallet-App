package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.exception.AuthenticationException;
import ru.zenclass.ylab.model.dto.LoginResponseDTO;
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
import java.util.Map;
import java.util.Optional;

/**
 * Сервлет, предназначенный для авторизации пользователей.
 * <p>
 * После успешной авторизации пользователь получает JSON-ответ с сообщением об успехе, данными пользователя
 * и JWT-токеном. В случае ошибки авторизации возвращает сообщение об ошибке.
 * </p>
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/players/login"})
public class LoginServlet extends HttpServlet {
    private PlayerService playerService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() {
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
        this.playerService = new PlayerServiceImpl(playerRepository);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            RegisterPlayerDTO registerPlayerDTO = mapper.readValue(req.getReader(), RegisterPlayerDTO.class);
            Player playerEntity = PlayerMapper.INSTANCE.toPlayerEntity(registerPlayerDTO);
            LoginResponseDTO response = playerService.authenticateAndGenerateToken(playerEntity.getUsername(),
                    playerEntity.getPassword());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(mapper.writeValueAsString(response));
        } catch (AuthenticationException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(mapper.writeValueAsString(Map.of("ошибка", e.getMessage())));
        }
    }
}