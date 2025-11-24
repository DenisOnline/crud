package pet.project.shulzhenko.crud.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pet.project.shulzhenko.crud.security.handler.CustomAccessDeniedHandler;
import pet.project.shulzhenko.crud.security.handler.JwtAuthenticationEntryPoint;
import pet.project.shulzhenko.crud.security.jwt.JwtFilter;

import static pet.project.shulzhenko.crud.entity.Role.ADMIN;

/**
 * Конфигурация безопасности Spring Security.
 *
 * <p>Настраивает аутентификацию и авторизацию, фильтры JWT, обработку отказа в доступе
 * и неавторизованных запросов, а также стратегию сессий Stateless.</p>
 *
 * <p>Настраивает доступ к различным эндпоинтам:</p>
 * <ul>
 *     <li>Публичные: регистрация, логин, обновление токена, Swagger</li>
 *     <li>Доступные только ADMIN: пользователи, просмотр и изменение заказов</li>
 *     <li>Остальные запросы требуют аутентификации</li>
 * </ul>
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    public static final String API_REGISTER = "/api/auth/register";
    public static final String API_LOGIN = "/api/auth/login";
    public static final String API_REFRESH = "/api/auth/refresh";
    public static final String API_USERS = "/api/users/**";
    public static final String API_SWAGGER_UI = "/swagger-ui/**";
    public static final String API_SWAGGER_DOCS = "/v3/api-docs/**";
    public static final String API_GET_USER_ORDERS = "/api/orders/all";
    public static final String API_PUT_ORDERS = "/api/orders/*";

    private final JwtFilter jwtFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Настройка цепочки фильтров безопасности.
     *
     * @param http объект {@link HttpSecurity}
     * @return {@link SecurityFilterChain} с настройками безопасности
     * @throws Exception если не удалось настроить фильтры
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(API_REGISTER, API_LOGIN, API_REFRESH, API_SWAGGER_UI, API_SWAGGER_DOCS).permitAll()
                        .requestMatchers(API_USERS).hasAuthority(ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.GET, API_GET_USER_ORDERS).hasAuthority(ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.PUT, API_PUT_ORDERS).hasAuthority(ADMIN.getAuthority())
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .build();
    }

    /**
     * Создает бин {@link PasswordEncoder} для хэширования паролей.
     *
     * @return {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}