package ru.zenclass.ylab.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.PlayerRepositoryImpl;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.PlayerServiceImpl;
import ru.zenclass.ylab.util.JwtUtil;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "PlayerBalanceServlet", urlPatterns = {"/balance"})
public class PlayerBalanceServlet extends HttpServlet {
    private final JwtUtil jwtUtil = new JwtUtil();
    private PlayerService playerService;

    @Override
    public void init() {
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
        this.playerService = new PlayerServiceImpl(playerRepository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        // Проверка и извлечение токена, аналогично примеру выше
        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Токен отсутствует или недействителен\"}");
            return;
        }
        token = token.substring(7);
        String username = jwtUtil.extractUsername(token);
        if (!jwtUtil.validateToken(token, username)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Токен недействителен\"}");
            return;
        }
        Optional<Player> playerOpt = playerService.findPlayerByUsername(username);
        if (playerOpt.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Пользователь не найден\"}");
            return;
        }
        Player player = playerOpt.get();
        // Возвращаем баланс игрока в ответе
        String jsonResponse = String.format(
                "{ \"message\": \"Текущий баланс игрока %s\", \"balance\": %s }",
                player.getUsername(),
                player.getBalance().toPlainString()
        );
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(jsonResponse);
    }
}
