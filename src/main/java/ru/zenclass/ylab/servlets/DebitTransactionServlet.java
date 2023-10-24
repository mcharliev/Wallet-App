package ru.zenclass.ylab.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.exception.NotEnoughMoneyException;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.model.mapper.TransactionMapper;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Сервлет для обработки дебетовых транзакций.
 * При успешной обработке дебетовой транзакции, отправляет ответ с статусом 201 (CREATED) и JSON-сообщением о успехе.
 * Если на счете недостаточно средств для проведения дебетовой транзакции, возвращает статус 400 (BAD REQUEST)
 * с сообщением о недостатке средств.
 */
@WebServlet(name = "DebitTransactionServlet", urlPatterns = {"/transactions/debit"})
public class DebitTransactionServlet extends HttpServlet {

//    /**
//     * Обрабатывает POST-запрос для создания дебетовой транзакции.
//     *
//     * @param req  запрос от клиента
//     * @param resp ответ сервера
//     * @throws IOException в случае ошибок ввода-вывода
//     */
//    @Override
//    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        Player player = getPlayerFromRequest(req, resp).orElse(null);
//        if (player == null) {
//            return;
//        }
//        BigDecimal creditAmount = getAmountFromRequest(req, resp).orElse(null);
//        if (creditAmount == null) {
//            return;
//        }
//        try {
//            Transaction transaction = transactionService.addDebitTransaction(player, creditAmount);
//            TransactionDTO transactionDTO = TransactionMapper.INSTANCE.toDTO(transaction);
//            String jsonResponse = String.format(
//                    "{ \"message\": \"Дебетовая транзакция успешно выполнена\", \"transaction\": %s }",
//                    mapper.writeValueAsString(transactionDTO));
//            resp.setStatus(HttpServletResponse.SC_CREATED);
//            resp.getWriter().write(jsonResponse);
//        } catch (NotEnoughMoneyException e) {
//            e.printStackTrace();
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            resp.getWriter().write("{\"error\": \"Недостаточно средств на счете\"}");
//        }
//    }
}