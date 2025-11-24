package pet.project.shulzhenko.crud.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pet.project.shulzhenko.crud.dto.response.ErrorResponse;
import pet.project.shulzhenko.crud.utils.ErrorType;

import java.io.IOException;
import java.time.Instant;

/**
 * Точка входа для обработки неавторизованных запросов.
 *
 * <p>Реализует интерфейс {@link AuthenticationEntryPoint} и вызывается в Spring Security,
 * когда пользователь пытается получить доступ к защищённому ресурсу без действительного JWT-токена.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String UNAUTHORIZED_ACCESS_URI = "Unauthorized access. uri={}";
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn(UNAUTHORIZED_ACCESS_URI, request.getRequestURI());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorType.JWT_VALIDATION_FAILED.name())
                .message(ErrorType.JWT_VALIDATION_FAILED.getMessage())
                .status(ErrorType.JWT_VALIDATION_FAILED.getStatus().value())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}