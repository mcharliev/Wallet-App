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


@WebServlet(name = "CreditTransactionServlet", urlPatterns = {"/transactions/credit"})
public class CreditTransactionServlet extends BaseTransactionServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
