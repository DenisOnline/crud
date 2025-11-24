package pet.project.shulzhenko.crud.security.jwt.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pet.project.shulzhenko.crud.dto.JwtAuthenticationDto;
import pet.project.shulzhenko.crud.exeption.CustomJwtException;
import pet.project.shulzhenko.crud.security.jwt.JwtService;
import pet.project.shulzhenko.crud.utils.CustomClaims;
import pet.project.shulzhenko.crud.utils.ErrorType;
import pet.project.shulzhenko.crud.utils.JwtClaimUtils;
import pet.project.shulzhenko.crud.utils.TokenType;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Сервис для работы с JWT-токенами.
 *
 * <p>Предоставляет методы генерации access и refresh токенов,
 * проверки их валидности и извлечения имени пользователя из токена.</p>
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-ttl-hours}")
    private long accessTtlHours;

    @Value("${jwt.refresh-ttl-days}")
    private long refreshTtlDays;

    @Override
    public JwtAuthenticationDto generateAuthToken(String username) {
        return JwtAuthenticationDto.builder()
                .accessToken(generateToken(username, Duration.ofHours(accessTtlHours), TokenType.ACCESS))
                .refreshToken(generateToken(username, Duration.ofDays(refreshTtlDays), TokenType.REFRESH))
                .build();
    }

    @Override
    public JwtAuthenticationDto refreshAccessToken(String refreshToken) {
        Claims claims = JwtClaimUtils.getClaims(refreshToken, getSignInKey());
        String type = JwtClaimUtils.getType(claims);
        if (!TokenType.REFRESH.getMessage().equals(type)) throw new CustomJwtException(ErrorType.UNEXPECTED_TOKEN_TYPE, type);

        String email = JwtClaimUtils.getSubject(claims);
        String newAccess = generateToken(email, Duration.ofHours(accessTtlHours), TokenType.ACCESS);

        return JwtAuthenticationDto.builder()
                .accessToken(newAccess)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (JwtException e) {
            log.error(ErrorType.JWT_VALIDATION_FAILED, e);
        }
        return false;
    }

    /**
     * Генерирует JWT-токен с указанным временем жизни и типом.
     *
     * @param username имя пользователя
     * @param ttl время жизни токена
     * @param tokenType тип токена (ACCESS или REFRESH)
     * @return JWT-токен
     */
    private String generateToken(String username, Duration ttl, TokenType tokenType) {
        Instant now = Instant.now();
        Instant exp = now.plus(ttl);
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim(CustomClaims.TYPE.getMessage(), tokenType.getMessage())
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Получает секретный ключ для подписи JWT.
     *
     * @return {@link SecretKey} для подписи
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}