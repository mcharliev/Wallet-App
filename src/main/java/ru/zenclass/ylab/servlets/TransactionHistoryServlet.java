package ru.zenclass.ylab.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.mapper.TransactionMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "TransactionHistoryServlet", urlPatterns = {"/transaction-history"})
public class TransactionHistoryServlet extends BaseTransactionServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<Player> playerOpt = validateTokenAndGetPlayer(req, resp);
        if (playerOpt.isEmpty()) {
            return; // Если токен недействителен или пользователь не найден, метод validateTokenAndGetPlayer уже обработал это.
        }
        Player player = playerOpt.get();

        // Запрос истории транзакций
        List<Transaction> transactions = transactionService.viewTransactionHistory(player.getId(), player.getUsername());
        List<TransactionDTO> dtoList = transactions.stream()
                .map(TransactionMapper.INSTANCE::toDTO).toList();

        // Формируем JSON-ответ
        String jsonResponse;
        if (transactions.isEmpty()) {
            jsonResponse = String.format(
                    "{ \"message\": \"У игрока: %s нету платежной истории\" }",
                    player.getUsername());
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            jsonResponse = String.format(
                    "{ \"message\": \"История игрока: %s\", \"transactions\": %s }",
                    player.getUsername(), mapper.writeValueAsString(dtoList));
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        resp.getWriter().write(jsonResponse);
    }
}

