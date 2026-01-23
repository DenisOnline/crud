package pet.project.shulzhenko.crud.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

/**
 * Утилитарный класс для работы с JWT claim'ами.
 *
 * <p>
 * Содержит статические методы для:
 * <ul>
 *     <li>парсинга JWT-токена</li>
 *     <li>извлечения стандартных и пользовательских claim'ов</li>
 * </ul>
 */
public final class JwtClaimUtils {

    private JwtClaimUtils() {}

    /**
     * Извлекает {@link Claims} из подписанного JWT-токена.
     *
     * @param token JWT-токен
     * @param key секретный ключ для проверки подписи
     * @return объект {@link Claims}, содержащий payload токена
     * @throws io.jsonwebtoken.JwtException если токен невалиден
     */
    public static Claims getClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Извлекает тип токена из JWT claim'ов.

     *
     * @param claims claim'ы JWT
     * @return тип токена {@link TokenType} или {@code null}, если claim отсутствует
     */
    public static String getType(Claims claims) {
        Object val = claims.get(CustomClaims.TYPE.getMessage());
        return val != null ? val.toString() : null;
    }

    /**
     * Возвращает subject из JWT claim'ов.
     *
     * @param claims claim'ы JWT
     * @return subject токена
     */
    public static String getSubject(Claims claims) {
        return claims.getSubject();
    }
}