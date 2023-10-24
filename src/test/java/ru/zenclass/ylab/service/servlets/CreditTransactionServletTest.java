package ru.zenclass.ylab.service.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.Transaction;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.TransactionService;
import ru.zenclass.ylab.servlets.CreditTransactionServlet;
import ru.zenclass.ylab.util.JwtUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Optional;

public class CreditTransactionServletTest {

//    @Test
//    public void testDoPostValidCreditTransaction() throws Exception {
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        HttpServletResponse response = mock(HttpServletResponse.class);
//        PlayerService mockPlayerService = mock(PlayerService.class);
//        TransactionService mockTransactionService = mock(TransactionService.class);
//        JwtUtil mockJwtUtil = mock(JwtUtil.class);
//
//        Player player = new Player();
//        player.setUsername("testName");
//        Transaction transaction = new Transaction(); // Assuming you have a default constructor or you can set properties as needed
//        String token = "Bearer your_token_here";
//
//        when(request.getHeader("Authorization")).thenReturn(token);
//        when(mockPlayerService.findPlayerByUsername(anyString())).thenReturn(Optional.of(player));
//        when(mockJwtUtil.validateToken(eq(token.substring(7)), eq("testName"))).thenReturn(true);
//        when(mockJwtUtil.extractUsername(anyString())).thenReturn("testName");
//        when(mockTransactionService.addCreditTransaction(eq(player), any(BigDecimal.class))).thenReturn(transaction);
//
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter writer = new PrintWriter(stringWriter);
//        when(response.getWriter()).thenReturn(writer);
//
//        CreditTransactionServlet servlet = new CreditTransactionServlet() {
//            @Override
//            protected JwtUtil createJwtUtil() {
//                return mockJwtUtil;
//            }
//
//            @Override
//            public void init() {
//                super.init();
//                this.playerService = mockPlayerService;
//                this.transactionService = mockTransactionService;
//                this.jwtUtil = mockJwtUtil;
//            }
//
//            @Override
//            public Validator initValidator() {
//                return mock(Validator.class);
//            }
//        };
//
//        servlet.init();
//        servlet.doPost(request, response);
//
//
//        assertTrue(stringWriter.toString().contains("Транзакция успешно выполнена"));
//        verify(response).setStatus(HttpServletResponse.SC_CREATED);
//    }
}
