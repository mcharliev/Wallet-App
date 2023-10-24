package ru.zenclass.ylab.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.util.JwtUtil;

import java.util.Optional;

public class AuthService {
    private final PlayerService playerService;
    private final JwtUtil jwtUtil;
    private final static Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(PlayerService playerService, JwtUtil jwtUtil) {
        this.playerService = playerService;
        this.jwtUtil = jwtUtil;
        log.info("AuthService initialized.");
    }

    public Optional<String> extractUsernameFromToken(String token) {
        log.info("Attempting to extract username from token.");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("Token is null or does not start with 'Bearer '.");
            return Optional.empty();
        }
        token = token.substring(7);
        try {
            String username = jwtUtil.extractUsername(token);
            log.info("Extracted username: {}", username);
            return Optional.of(username);
        } catch (SignatureException | ExpiredJwtException ex) {
            log.error("Error extracting username from token.", ex);
            return Optional.empty();
        }
    }

    public boolean validateToken(String token, String username) {
        log.info("Validating token for username: {}", username);
        boolean isValid = jwtUtil.validateToken(token, username);
        log.info("Token is valid for username {}: {}", username, isValid);
        return isValid;
    }

    public Optional<Player> validateTokenAndGetPlayer(String token) {
        log.info("Validating token and attempting to get Player.");
        Optional<String> usernameOpt = extractUsernameFromToken(token);
        if (usernameOpt.isEmpty()) {
            log.warn("Username not extracted from token.");
            return Optional.empty();
        }
        String username = usernameOpt.get();
        if (!validateToken(token, username)) {
            log.warn("Token validation failed for username: {}", username);
            return Optional.empty();
        }

        // Get player using the PlayerService and directly return the result.
        Optional<Player> playerOpt = playerService.findPlayerByUsername(username);
        if (playerOpt.isPresent()) {
            log.info("Retrieved player for username: {}", username);
        } else {
            log.warn("No player found for username: {}", username);
        }

        return playerOpt;
    }
}

