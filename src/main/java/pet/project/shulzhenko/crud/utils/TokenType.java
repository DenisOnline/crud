package pet.project.shulzhenko.crud.utils;

import lombok.Getter;

/**
 * Перечисление типов JWT-токенов, используемых в системе.
 * Значения enum сохраняются в JWT как пользовательский claim.
 */
@Getter
public enum TokenType {

    /**
     * Access-токен, используемый для авторизации запросов.
     */
    ACCESS("access"),
    /**
     * Refresh-токен, используемый для обновления access-токена.
     */
    REFRESH("refresh");

    private final String message;

    TokenType(String message) {
        this.message = message;
    }
}