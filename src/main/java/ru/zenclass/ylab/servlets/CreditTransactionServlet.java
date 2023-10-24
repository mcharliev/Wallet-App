package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.model.dto.AmountDTO;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.mapper.TransactionMapper;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.PlayerRepositoryImpl;
import ru.zenclass.ylab.repository.TransactionRepository;
import ru.zenclass.ylab.repository.TransactionRepositoryImpl;
import ru.zenclass.ylab.service.*;
import ru.zenclass.ylab.util.JwtUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

/**
 * Сервлет для обработки кредитных транзакций.
 * При успешном выполнении кредитной транзакции, отправляет ответ с статусом 201 (CREATED) и JSON-сообщением о успехе.
 * В случае ошибки возвращает статус 400 (BAD REQUEST) с сообщением об ошибке.
 * </p>
 */
@WebServlet(name = "CreditTransactionServlet", urlPatterns = {"/transactions/credit"})
public class CreditTransactionServlet extends HttpServlet {

    private TransactionService transactionService;
    protected ObjectMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(CreditTransactionServlet.class);
    protected JwtUtil jwtUtil = new JwtUtil();
    protected PlayerService playerService;
    protected Validator validator;
    @Override
    public void init() {
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        TransactionRepository transactionRepository = new TransactionRepositoryImpl(connectionManager);
        PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
        this.playerService = new PlayerServiceImpl(playerRepository);  // здесь исправлено
        this.transactionService = new TransactionServiceImpl(transactionRepository, playerService);
        mapper = new ObjectMapper();
        validator = initValidator();
    }
    public Validator initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        return factory.getValidator();
    }
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Player player = getPlayerFromRequest(req, resp).orElse(null);
        if (player == null) {
            return;
        }
        BigDecimal creditAmount = getAmountFromRequest(req, resp).orElse(null);
        if (creditAmount == null) {
            return;
        }
        try {
            Transaction savedTransaction = transactionService.addCreditTransaction(player, creditAmount);
            TransactionDTO transactionDTO = TransactionMapper.INSTANCE.toDTO(savedTransaction);
            String jsonResponse = String.format(
                    "{ \"message\": \"Транзакция успешно выполнена\", \"transaction\": %s }",
                    mapper.writeValueAsString(transactionDTO));
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Ошибка при выполнении транзакции\"}");
        }
    }



    protected Optional<Player> validateTokenAndGetPlayer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Токен отсутствует или недействителен\"}");
            return Optional.empty();
        }
        token = token.substring(7);
        try {
            String username = jwtUtil.extractUsername(token);
            if (!jwtUtil.validateToken(token, username)) {
                System.out.println("Token validation failed for token: " + token + " and username: " + username);
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
        } catch (SignatureException ex) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Токен недействителен\"}");
            return Optional.empty();
        } catch (ExpiredJwtException ex) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Токен истек\"}");
            return Optional.empty();
        }
    }
    protected Optional<Player> getPlayerFromRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<Player> playerOpt = validateTokenAndGetPlayer(req, resp);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }
        return playerOpt;
    }
    protected Optional<BigDecimal> getAmountFromRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            AmountDTO amountDTO = mapper.readValue(req.getReader(), AmountDTO.class);
            Set<ConstraintViolation<AmountDTO>> violations = validator.validate(amountDTO);
            if (!violations.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Некорректное значение суммы\"}");
                return Optional.empty();
            }
            return Optional.of(amountDTO.getAmount());
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Ошибка чтения данных\"}");
            return Optional.empty();
        }
    }
}
