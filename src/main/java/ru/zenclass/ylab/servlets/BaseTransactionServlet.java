package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.model.dto.AmountDTO;
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
import java.util.Set;

/**
 * Базовый сервлет для обработки транзакций.
 * <p>
 * Этот абстрактный класс содержит общую логику и утилиты для работы с транзакциями,
 * такие как валидация токенов, извлечение данных о игроке и обработка суммы транзакции.
 * </p>
 */
public abstract class BaseTransactionServlet extends HttpServlet {
    protected ObjectMapper mapper;
    protected JwtUtil jwtUtil;
    protected TransactionService transactionService;
    protected PlayerService playerService;
    protected Validator validator;

    /**
     * Инициализирует сервлет, настраивая необходимые сервисы и валидаторы.
     */
    @Override
    public void init() {
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
        TransactionRepository transactionRepository = new TransactionRepositoryImpl(connectionManager);
        this.playerService = new PlayerServiceImpl(playerRepository);
        this.transactionService = new TransactionServiceImpl(transactionRepository, playerService);
        mapper = new ObjectMapper();
        validator = initValidator();
        this.jwtUtil = createJwtUtil();
    }
    protected JwtUtil createJwtUtil() {
        return new JwtUtil();
    }
    public Validator initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    /**
     * Валидирует токен из заголовка запроса и возвращает соответствующего игрока.
     *
     * @param req  Запрос от клиента.
     * @param resp Ответ клиенту.
     * @return Игрок {@link Optional<Player>} , если токен действителен; иначе {@link Optional#empty()}.
     * @throws IOException В случае ошибок ввода-вывода.
     */
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

    /**
     * Возвращает объект игрока из запроса после валидации токена.
     *
     * @param req  Запрос от клиента.
     * @param resp Ответ клиенту.
     * @return Игрок {@link Optional<Player>}, если токен действителен; иначе {@link Optional#empty()}.
     * @throws IOException В случае ошибок ввода-вывода.
     */
    protected Optional<Player> getPlayerFromRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<Player> playerOpt = validateTokenAndGetPlayer(req, resp);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }
        return playerOpt;
    }

    /**
     * Извлекает сумму транзакции из запроса и валидирует ее.
     * @param req  Запрос от клиента.
     * @param resp Ответ клиенту.
     * @return Сумма транзакции в случае успешной валидации {@link Optional<BigDecimal>};
     * иначе {@link Optional#empty()}.
     * @throws IOException В случае ошибок ввода-вывода.
     */
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
