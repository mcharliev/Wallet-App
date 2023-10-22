package ru.zenclass.ylab.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Утилитный класс для операций с JSON Web Token (JWT), таких как создание, проверка и извлечение данных.
 */
public class JwtUtil {

    /** Секретный ключ, используемый для подписи и верификации JWT. */
    private final String SECRET_KEY;

    public JwtUtil() {
        DatabaseConnectionManager dbManager = new DatabaseConnectionManager();
        SECRET_KEY = dbManager.getProperties().getProperty("secretKey");
        if (SECRET_KEY == null) {
            throw new RuntimeException("Секретный ключ в application.properties не найден");
        }
    }

    /**
     * Генерирует JWT токен на основе предоставленного имени пользователя.
     * @param username имя пользователя, которое будет включено в токен
     * @return сгенерированный JWT токен в формате {@link String}
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Создаёт JWT, используя предоставленные данные.
     * @param claims  данные для включения в токен
     * @param subject субъект токена (обычно имя пользователя)
     * @return сгенерированный JWT токен в формате {@link String}
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    /**
     * Проверяет действительность предоставленного JWT.
     * @param token    JWT для проверки
     * @param username ожидаемое имя пользователя в токене
     * @return {@code true}, если токен действителен, иначе {@code false}
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Извлекает имя пользователя из предоставленного JWT.
     * @param token JWT
     * @return имя пользователя, включённое в токен в формате {@link String}
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Извлекает дату истечения срока действия из предоставленного JWT.
     * @param token JWT
     * @return дата истечения срока действия токена в формате {@link Date}
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Общий метод для извлечения конкретных данных из предоставленного JWT.
     * @param token          JWT
     * @param claimsResolver функция для определения, какие данные извлекать
     * @param <T>            тип извлекаемых данных
     * @return извлечённые данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает все данные из предоставленного JWT.
     * @param token JWT
     * @return все данные, включённые в токен в формате {@link Claims}
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    /**
     * Проверяет, истекло ли время действия предоставленного JWT.
     * @param token JWT
     * @return {@code true}, если время действия токена истекло, иначе {@code false}
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}