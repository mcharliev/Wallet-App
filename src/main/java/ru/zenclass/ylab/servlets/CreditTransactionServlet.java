package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.mapper.TransactionMapper;
import ru.zenclass.ylab.service.AuthService;
import ru.zenclass.ylab.service.RequestService;
import ru.zenclass.ylab.service.ServiceLocator;
import ru.zenclass.ylab.service.TransactionService;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Сервлет для обработки кредитных транзакций.
 * При успешном выполнении кредитной транзакции, отправляет ответ с статусом 201 (CREATED) и JSON-сообщением о успехе.
 * В случае ошибки возвращает статус 400 (BAD REQUEST) с сообщением об ошибке.
 * </p>
 */
@WebServlet(name = "CreditTransactionServlet", urlPatterns = {"/transactions/credit"})
public class CreditTransactionServlet extends HttpServlet {

    private TransactionService transactionService;
    private ObjectMapper mapper = new ObjectMapper();
    private AuthService authService;
    private RequestService requestService;

    @Override
    public void init() {
        this.transactionService = ServiceLocator.getTransactionService();
        this.authService = ServiceLocator.getAuthService();
        this.requestService = ServiceLocator.getRequestService();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Player player = authService.getPlayerFromRequest(req, resp).orElse(null);
        if (player == null) {
            return;
        }
        BigDecimal creditAmount = requestService.getAmountFromRequest(req, resp).orElse(null);
        if (creditAmount == null) {
            return;
        }
        try {
            Transaction savedTransaction = transactionService.addCreditTransaction(player, creditAmount);
            TransactionDTO transactionDTO = TransactionMapper.INSTANCE.toDTO(savedTransaction);
            String jsonResponse = mapper.writeValueAsString(transactionDTO);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Ошибка при выполнении транзакции\"}");
        }
    }
}
