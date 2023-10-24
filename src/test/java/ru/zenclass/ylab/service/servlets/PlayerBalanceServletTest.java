package ru.zenclass.ylab.service.servlets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.servlets.PlayerBalanceServlet;
import ru.zenclass.ylab.util.JwtUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PlayerBalanceServletTest {

//    @Test
//    public void testDoGetValidPlayer() throws Exception {
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        HttpServletResponse response = mock(HttpServletResponse.class);
//        PlayerService mockPlayerService = mock(PlayerService.class);
//        JwtUtil mockJwtUtil = mock(JwtUtil.class);
//
//        Player player = new Player();
//        player.setUsername("testName");
//        player.setBalance(new BigDecimal("1000"));
//        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNZXJkYW4iLCJleHAiOjE2OTgwOTY0ODYsImlhdCI6MTY5ODA2MDQ4Nn0.m18FKnb9vtwhej7ZspG2MC8mHTl8kOV8wSySAAobR-I";
//
//        when(request.getHeader("Authorization")).thenReturn(token);
//        when(mockPlayerService.findPlayerByUsername(anyString())).thenReturn(Optional.of(player));
//        when(mockPlayerService.getPlayerBalanceInfo(player)).thenReturn("{\"balance\": 1000}");
//        when(mockJwtUtil.validateToken(eq(token.substring(7)), eq("testName"))).thenReturn(true);
//        when(mockJwtUtil.extractUsername(anyString())).thenReturn("testName");
//
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter writer = new PrintWriter(stringWriter);
//        when(response.getWriter()).thenReturn(writer);
//        PlayerBalanceServlet servlet = new PlayerBalanceServlet() {
//            @Override
//            protected JwtUtil createJwtUtil() {
//                return mockJwtUtil;
//            }
//
//            @Override
//            public void init() {
//                super.init();
//                this.playerService = mockPlayerService;
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
//        servlet.doGet(request, response);
//
//        assertEquals("{\"balance\": 1000}", stringWriter.toString());
//        verify(response).setStatus(HttpServletResponse.SC_OK);
//    }
}