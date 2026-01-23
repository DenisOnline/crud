package pet.project.shulzhenko.crud.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pet.project.shulzhenko.crud.security.handler.JwtAuthenticationEntryPoint;
import pet.project.shulzhenko.crud.security.CustomUserDetails;
import pet.project.shulzhenko.crud.security.CustomUserServiceImpl;

import java.io.IOException;

/**
 * Фильтр JWT для Spring Security.
 *
 * <p>Проверяет наличие и валидность JWT в заголовке Authorization.
 * Если токен валиден, устанавливает {@link CustomUserDetails} в SecurityContext.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserServiceImpl customUserService;
    private final ObjectMapper objectMapper;

    /**
     * Обрабатывает каждый входящий HTTP-запрос для проверки JWT-токена.
     *
     * <p>Метод выполняет следующие шаги:</p>
     * <ol>
     *     <li>Извлекает JWT-токен из заголовка Authorization.</li>
     *     <li>Проверяет, существует ли токен и является ли он валидным.</li>
     *     <li>Если токен валиден, загружает пользователя через и устанавливает {@link CustomUserDetails} в {@link SecurityContextHolder} для дальнейшей аутентификации в Spring Security.</li>
     *     <li>Передаёт управление следующему фильтру в цепочке</li>
     *     <li>В случае возникновения {@link AuthenticationException} очищает {@link SecurityContextHolder}
     *         и вызывает {@link JwtAuthenticationEntryPoint} для обработки ошибки аутентификации.</li>
     * </ol>
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @param filterChain цепочка фильтров
     * @throws ServletException при ошибках фильтрации
     * @throws IOException при ошибках ввода-вывода
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        try {
            if (token != null && jwtService.validateJwtToken(token)) {
                setCustomUserDetailsToSecurityContextHolder(token);
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            AuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint(objectMapper);
            entryPoint.commence(request, response, ex);
        }
    }

    /**
     * Извлекает JWT-токен из заголовка Authorization.
     *
     * @param request HTTP-запрос
     * @return JWT-токен или {@code null} если отсутствует
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return request.getHeader("Authorization").substring(7);
        } else {
            return null;
        }
    }

    /**
     * Устанавливает пользователя в SecurityContext на основе токена.
     *
     * @param token JWT-токен
     */
    private void setCustomUserDetailsToSecurityContextHolder(String token) {
        String email = jwtService.getUsernameFromToken(token);
        CustomUserDetails customUserDetails = customUserService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customUserDetails,
                null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
