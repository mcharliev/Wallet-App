package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.model.entity.Player;
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
import java.math.BigDecimal;
import java.util.Optional;

public abstract class BaseTransactionServlet extends HttpServlet {
    protected ObjectMapper mapper;
    protected final JwtUtil jwtUtil = new JwtUtil();
    protected TransactionService transactionService;
    protected PlayerService playerService;

    @Override
    public void init() {
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
        TransactionRepository transactionRepository = new TransactionRepositoryImpl(connectionManager);
        this.playerService = new PlayerServiceImpl(playerRepository);
        this.transactionService = new TransactionServiceImpl(transactionRepository, playerService);
        mapper = new ObjectMapper();
    }

    public Optional<Player> validateTokenAndGetPlayer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Токен отсутствует или недействителен\"}");
            return Optional.empty();
        }
        token = token.substring(7);
        String username = jwtUtil.extractUsername(token);
        if (!jwtUtil.validateToken(token, username)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Токен недействителен\"}");
            return Optional.empty();
        }
        Optional<Player> playerOpt = playerService.findPlayerByUsername(username);
        if (playerOpt.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Пользователь не найден\"}");
            return Optional.empty();
        }
        return playerOpt;
    }
    protected Optional<Player> getPlayerFromRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<Player> playerOpt = validateTokenAndGetPlayer(req, resp);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }
        return playerOpt;
    }
    protected Optional<BigDecimal> getAmountFromRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String amountString = req.getParameter("amount");
        try {
            BigDecimal amount = new BigDecimal(amountString);
            System.out.println("Received amount: " + amountString);
            return Optional.of(amount);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Некорректное значение суммы\"}");
            return Optional.empty();
        }
    }
}
