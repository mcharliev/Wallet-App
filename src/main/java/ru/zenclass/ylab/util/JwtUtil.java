package ru.zenclass.ylab.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * Утилитный класс для операций с JSON Web Token (JWT), таких как создание, проверка и извлечение данных.
 */
public class JwtUtil {
    private final String SECRET_KEY;
    private static Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil() {
        DatabaseConnectionManager dbManager = new DatabaseConnectionManager();
        SECRET_KEY = dbManager.getProperties().getProperty("secretKey");
        if (SECRET_KEY == null) {
            throw new RuntimeException("Секретный ключ в application.properties не найден");
        }
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        boolean isValid = extractedUsername.equals(username) && !isTokenExpired(token);
        log.info("Validating token for username '{}'. Token valid: {}", username, isValid);
        return isValid;
    }

    public String extractUsername(String token) {
        String username = extractClaim(token, Claims::getSubject);
        log.info("Extracted username from token: {}", username);
        return username;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        log.info("Extracted claims from token: {}", claims);
        return claims;
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}