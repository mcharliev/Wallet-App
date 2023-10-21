package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.mapper.TransactionMapper;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.PlayerRepositoryImpl;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.repository.TransactionRepositoryImpl;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.PlayerServiceImpl;
import ru.zenclass.ylab.service.TransactionService;
import ru.zenclass.ylab.service.TransactionServiceImpl;
import ru.zenclass.ylab.util.JwtUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "TransactionHistoryServlet", urlPatterns = {"/transaction-history"})
public class TransactionHistoryServlet extends HttpServlet {
    private ObjectMapper mapper;
    private final JwtUtil jwtUtil = new JwtUtil();
    private TransactionService transactionService;

    private PlayerService playerService;

    @Override
    public void init() {
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
        TransactionRepository transactionRepository = new TransactionRepositoryImpl(connectionManager);
        this.playerService = new PlayerServiceImpl(playerRepository);
        this.transactionService = new TransactionServiceImpl(transactionRepository, playerService);
        mapper = new ObjectMapper();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        // 1. Проверка токена
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

        // 2. Запрос истории транзакций
        List<Transaction> transactions = transactionService.viewTransactionHistory(player.getId(), username);
        List<TransactionDTO> dtoList = transactions.stream()
                .map(TransactionMapper.INSTANCE::toDTO).toList();
        // 3. Формируем JSON-ответ.
        String jsonResponse;
        if (transactions.isEmpty()) {
            jsonResponse = String.format(
                    "{ \"message\": \"У игрока: %s нету платежной истории\" }",
                    username);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            jsonResponse = String.format(
                    "{ \"message\": \"История игрока: %s\", \"transactions\": %s }",
                    username, mapper.writeValueAsString(dtoList));
        }
        resp.getWriter().write(jsonResponse);
    }
}
