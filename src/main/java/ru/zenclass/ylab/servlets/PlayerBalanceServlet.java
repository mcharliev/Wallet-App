package ru.zenclass.ylab.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.service.AuthService;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.ServiceLocator;

import java.io.IOException;


/**
 * Сервлет, предназначенный для получения текущего баланса игрока.
 * <p>
 * После успешной валидации токена и получения игрока, сервлет возвращает JSON-ответ с текущим балансом пользователя.
 * </p>
 */
@WebServlet(name = "PlayerBalanceServlet", urlPatterns = {"/players/balance"})
public class PlayerBalanceServlet extends HttpServlet {

    private PlayerService playerService;
    private AuthService authService;
    @Override
    public void init() {
        this.playerService = ServiceLocator.getPlayerService();
        this.authService = ServiceLocator.getAuthService();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Player player = authService.getPlayerFromRequest(req, resp).orElse(null);
        if (player == null) {
            return;
        }
        try {
            String jsonResponse = playerService.getPlayerBalanceInfo(player);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Ошибка при получении баланса игрока\"}");
        }
    }
}

