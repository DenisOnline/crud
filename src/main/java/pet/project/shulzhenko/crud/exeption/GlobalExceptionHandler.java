package pet.project.shulzhenko.crud.exeption;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pet.project.shulzhenko.crud.dto.response.ErrorResponse;
import pet.project.shulzhenko.crud.utils.ErrorType;
import pet.project.shulzhenko.crud.utils.LogType;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 *
 * <p>Обрабатывает различные типы исключений и формирует стандартный JSON-ответ {@link ErrorResponse}:
 * <ul>
 *     <li>{@link ServiceException}</li>
 *     <li>{@link MethodArgumentNotValidException}</li>
 *     <li>{@link HttpMessageNotReadableException}</li>
 *     <li>{@link AuthorizationDeniedException}</li>
 *     <li>Любые другие {@link Exception}</li>
 * </ul>
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработчик исключений {@link ServiceException}.
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex, HttpServletRequest request) {
        ErrorType errorType = ex.getErrorType();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorType.name())
                .message(ex.getMessage())
                .status(errorType.getStatus().value())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        log.error(LogType.SERVICE_EXCEPTION.getMessage(),
                ex.getClass().getSimpleName(),
                ex.getErrorType().name(),
                ex.getMessage(),
                ex
        );

        return ResponseEntity.status(errorType.getStatus()).body(errorResponse);
    }

    /**
     * Обработчик исключений валидации {@link MethodArgumentNotValidException}.
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn(LogType.VALIDATION_FAILED.getMessage(), errors);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorType.VALIDATION_FAILED.name())
                .message(errors)
                .status(ErrorType.VALIDATION_FAILED.getStatus().value())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Обработчик исключений связанные с неправильным указание значения в JSON для enum {@link HttpMessageNotReadableException}.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEnum(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Throwable cause = ex.getCause();
        String message;

        if (cause instanceof InvalidFormatException invalidFormatEx
            && invalidFormatEx.getTargetType().isEnum()) {
            String acceptedValues = Arrays.stream(invalidFormatEx.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            message = String.format(ErrorType.INVALID_VALUE_S_ACCEPTED_VALUES_S.getMessage(),
                    invalidFormatEx.getValue(), acceptedValues);
        } else {
            message = ex.getMessage();
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorType.INVALID_REQUEST.name())
                .message(message)
                .status(ErrorType.INVALID_REQUEST.getStatus().value())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        log.warn(LogType.INVALID_REQUEST_BODY.getMessage(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Обработчик исключений {@link AuthorizationDeniedException}.
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorType.NO_PERMISSION_ADMIN_OR_OWNER.name())
                .message(ErrorType.NO_PERMISSION_ADMIN_OR_OWNER.getMessage())
                .status(ErrorType.NO_PERMISSION_ADMIN_OR_OWNER.getStatus().value())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }

    /**
     * Обработчик любых других исключений.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorType.NO_PERMISSION_ADMIN_OR_OWNER.name())
                .message(ex.getMessage())
                .status(ErrorType.NO_PERMISSION_ADMIN_OR_OWNER.getStatus().value())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        log.error(LogType.UNEXPECTED_EXCEPTION.getMessage(),
                ex.getMessage(),
                ex
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}