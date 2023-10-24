package ru.zenclass.ylab.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.util.JwtUtil;

import java.io.IOException;
import java.util.Optional;

public class AuthService {

    private final JwtUtil jwtUtil = new JwtUtil();
    private final PlayerService playerService;

    public AuthService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public Optional<Player> getPlayerFromRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = req.getHeader("Authorization");
        Optional<Player> playerOpt = validateTokenAndGetPlayer(token);
        if (playerOpt.isEmpty()) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Токен отсутствует или недействителен\"}");
        }
        return playerOpt;
    }

    private Optional<Player> validateTokenAndGetPlayer(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Optional.empty();
        }
        token = token.substring(7);
        try {
            String username = jwtUtil.extractUsername(token);
            if (!jwtUtil.validateToken(token, username)) {
                return Optional.empty();
            }
            return playerService.findPlayerByUsername(username);
        } catch (SignatureException | ExpiredJwtException ex) {
            return Optional.empty();
        }
    }
}

