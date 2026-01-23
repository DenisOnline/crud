package pet.project.shulzhenko.crud.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pet.project.shulzhenko.crud.dto.response.ErrorResponse;
import pet.project.shulzhenko.crud.utils.ErrorType;

import java.io.IOException;
import java.time.Instant;

/**
 * Обработчик отказа в доступе для Spring Security.
 *
 * <p>Реализует интерфейс {@link AccessDeniedHandler} и вызывается,
 * когда аутентифицированный пользователь пытается получить доступ к ресурсу,
 * на который у него нет прав.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    public static final String ACCESS_DENIED_USER_URI = "Access denied. user={}, uri={}";
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        log.warn(ACCESS_DENIED_USER_URI,
                request.getUserPrincipal(),
                request.getRequestURI()
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorType.NO_PERMISSION.name())
                .message(ErrorType.NO_PERMISSION.getMessage())
                .status(ErrorType.NO_PERMISSION.getStatus().value())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}