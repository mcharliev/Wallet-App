package ru.zenclass.ylab.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.model.entity.Player;

import java.io.IOException;
import java.util.Optional;

/**
 * Сервлет, предназначенный для получения текущего баланса игрока.
 * <p>
 * После успешной валидации токена и получения игрока, сервлет возвращает JSON-ответ с текущим балансом пользователя.
 * </p>
 */
@WebServlet(name = "PlayerBalanceServlet", urlPatterns = {"/players/balance"})
public class PlayerBalanceServlet extends BaseTransactionServlet {

    /**
     * Обрабатывает GET-запрос для получения баланса указанного игрока.
     *
     * @param req  запрос от клиента
     * @param resp ответ сервера
     * @throws IOException в случае ошибок ввода-вывода
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<Player> playerOpt = getPlayerFromRequest(req, resp);
        if (playerOpt.isEmpty()) {
            return;
        }
        Player player = playerOpt.get();
        String jsonResponse = playerService.getPlayerBalanceInfo(player);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(jsonResponse);
    }
}

