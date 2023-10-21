package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.exception.NotEnoughMoneyException;
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
import java.math.BigDecimal;
import java.util.Optional;

@WebServlet(name = "DebitTransactionServlet", urlPatterns = {"/debit"})
public class DebitTransactionServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        // Проверка и извлечение токена, как и в предыдущем сервлете
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

        // Чтение суммы дебетовой транзакции
        String amountString = req.getParameter("amount");
        BigDecimal debitAmount;
        try {
            debitAmount = new BigDecimal(amountString);
            System.out.println("Received amount: " + amountString);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Некорректное значение суммы\"}");
            return;
        }
        // Процесс дебетовой транзакции
        try {
            Transaction transaction = transactionService.addDebitTransaction(player, debitAmount);
            // Подразумевается, что если транзакция успешна, то у нас есть какой-то метод для извлечения последней транзакции
            TransactionDTO transactionDTO = TransactionMapper.INSTANCE.toDTO(transaction);
            String jsonResponse = String.format(
                    "{ \"message\": \"Дебетовая транзакция успешно выполнена\", \"transaction\": %s }",
                    mapper.writeValueAsString(transactionDTO));
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(jsonResponse);
        } catch (NotEnoughMoneyException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Ошибка при выполнении транзакции\"}");
        }
    }
}
