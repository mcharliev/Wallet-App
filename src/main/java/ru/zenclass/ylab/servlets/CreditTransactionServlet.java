package ru.zenclass.ylab.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.mapper.TransactionMapper;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Сервлет для обработки кредитных транзакций.
 * При успешном выполнении кредитной транзакции, отправляет ответ с статусом 201 (CREATED) и JSON-сообщением о успехе.
 * В случае ошибки возвращает статус 400 (BAD REQUEST) с сообщением об ошибке.
 * </p>
 */
@WebServlet(name = "CreditTransactionServlet", urlPatterns = {"/transactions/credit"})
public class CreditTransactionServlet extends BaseTransactionServlet {

    /**
     * Обрабатывает POST-запрос для создания кредитной транзакции.
     *
     * @param req  запрос от клиента
     * @param resp ответ сервера
     * @throws IOException в случае ошибок ввода-вывода
     */
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
}
