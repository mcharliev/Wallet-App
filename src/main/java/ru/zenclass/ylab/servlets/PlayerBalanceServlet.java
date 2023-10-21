package ru.zenclass.ylab.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.model.entity.Player;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "PlayerBalanceServlet", urlPatterns = {"/balance"})
public class PlayerBalanceServlet extends BaseTransactionServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<Player> playerOpt = validateTokenAndGetPlayer(req, resp);
        if (playerOpt.isEmpty()) {
            return;
        }
        Player player = playerOpt.get();
        String jsonResponse = String.format(
                "{ \"message\": \"Текущий баланс игрока %s\", \"balance\": %s }",
                player.getUsername(),
                player.getBalance().toPlainString()
        );
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(jsonResponse);
    }
}

