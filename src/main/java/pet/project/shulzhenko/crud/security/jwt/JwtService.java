package pet.project.shulzhenko.crud.security.jwt;

import pet.project.shulzhenko.crud.dto.JwtAuthenticationDto;
import pet.project.shulzhenko.crud.exeption.CustomJwtException;

public interface JwtService {

    /**
     * Генерирует JWT-токены (access и refresh) для указанного пользователя.
     *
     * @param username имя пользователя
     * @return DTO с access и refresh токенами
     */
    JwtAuthenticationDto generateAuthToken(String username);

    /**
     * Обновляет access-токен на основе refresh-токена.
     *
     * @param refreshToken refresh-токен
     * @return DTO с новым access-токеном и старым refresh-токеном
     * @throws CustomJwtException если тип токена некорректен
     */
    JwtAuthenticationDto refreshAccessToken(String refreshToken);

    /**
     * Извлекает имя пользователя из JWT-токена.
     *
     * @param token JWT-токен
     * @return имя пользователя
     */
    String getUsernameFromToken(String token);

    /**
     * Проверка валидности JWT-токена.
     *
     * @param token JWT-токен
     * @return {@code true}, если токен валиден, иначе {@code false}
     */
    boolean validateJwtToken(String token);
}
