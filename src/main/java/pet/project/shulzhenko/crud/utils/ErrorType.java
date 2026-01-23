package pet.project.shulzhenko.crud.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Перечисление типов ошибок приложения.
 * Применяется в кастомных исключениях и глобальном обработчике ошибок, содержит HTTP-статусы для REST-ответов.
 */
@Getter
public enum ErrorType {

    INVALID_VALUE_S_ACCEPTED_VALUES_S("Invalid value '%s'. Accepted values: [%s]", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED("VALIDATION_FAILED", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_SUCH_USER_BY_UUID("No such user by UUID: {%s}", HttpStatus.NOT_FOUND),
    USERNAME_OR_PASSWORD_IS_NOT_CORRECT("USERNAME_OR_PASSWORD_IS_NOT_CORRECT", HttpStatus.UNAUTHORIZED),
    NOT_FOUND_USERNAME("User with {%s} not found", HttpStatus.NOT_FOUND),
    USERNAME_ALREADY_EXISTS("Username: {%s} already exists", HttpStatus.CONFLICT),
    FAILED_RETRIEVE_USER_BY_USERNAME("Failed to retrieve user by username: {%s}", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_SUCH_ORDER_BY_UUID("No such order by UUID: {%s}", HttpStatus.NOT_FOUND),
    UNEXPECTED_TOKEN_TYPE("Unexpected token type", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN("Invalid refresh token", HttpStatus.UNAUTHORIZED),
    JWT_VALIDATION_FAILED("JWT_VALIDATION_FAILED", HttpStatus.UNAUTHORIZED),
    NO_PERMISSION("User does not have permission to perform this action", HttpStatus.FORBIDDEN),
    NO_PERMISSION_ADMIN_OR_OWNER("User is not admin or resource owner", HttpStatus.FORBIDDEN);

    /**
     * Текст сообщения об ошибке.
     */
    private final String message;
    /**
     * HTTP-статус, возвращаемый клиенту.
     */
    private final HttpStatus status;

    ErrorType(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}