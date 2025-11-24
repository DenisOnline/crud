package pet.project.shulzhenko.crud.unit.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pet.project.shulzhenko.crud.dto.JwtAuthenticationDto;
import pet.project.shulzhenko.crud.exeption.CustomJwtException;
import pet.project.shulzhenko.crud.security.jwt.JwtService;
import pet.project.shulzhenko.crud.security.jwt.impl.JwtServiceImpl;
import pet.project.shulzhenko.crud.utils.ErrorType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private static final String SECRET = "dGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQ=";
    private static final String USERNAME = "test@mail.com";
    public static final String ERROR_TYPE = "errorType";
    public static final String BROKEN_JWT_TOKEN = "broken.jwt.token";

    private JwtService jwtService;

    @BeforeEach
    void beforeEach() {
        jwtService = new JwtServiceImpl();

        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtService, "accessTtlHours", 1L);
        ReflectionTestUtils.setField(jwtService, "refreshTtlDays", 7L);
    }

    /**
     * Метод <b>generateAuthTokenSuccess</b> проверяет, что токены, полученные от метода <b>generateAuthToken</b>, валидны
     */
    @Test
    void generateAuthTokenSuccess() {
        JwtAuthenticationDto dto = jwtService.generateAuthToken(USERNAME);

        assertThat(dto).isNotNull();
        assertThat(dto.getAccessToken()).isNotBlank();
        assertThat(dto.getRefreshToken()).isNotBlank();
        assertThat(jwtService.validateJwtToken(dto.getAccessToken())).isTrue();
        assertThat(jwtService.validateJwtToken(dto.getRefreshToken())).isTrue();
        assertThat(jwtService.getUsernameFromToken(dto.getAccessToken())).isEqualTo(USERNAME);
    }

    /**
     * Метод <b>refreshAccessTokenSuccess</b> проверяет, что токены, полученные от метода <b>refreshAccessToken</b>, валидны
     */
    @Test
    void refreshAccessTokenSuccess() {
        JwtAuthenticationDto tokens = jwtService.generateAuthToken(USERNAME);
        JwtAuthenticationDto refreshed = jwtService.refreshAccessToken(tokens.getRefreshToken());
        String usernameFromToken = jwtService.getUsernameFromToken(refreshed.getAccessToken());

        assertThat(jwtService.validateJwtToken(tokens.getRefreshToken())).isTrue();
        assertThat(refreshed.getRefreshToken()).isEqualTo(tokens.getRefreshToken());
        assertThat(jwtService.validateJwtToken(refreshed.getAccessToken())).isTrue();
        assertThat(usernameFromToken).isEqualTo(USERNAME);
    }

    /**
     * Метод <b>refreshAccessTokenWrongTokenType</b> проверяет, что при попытке обновить <i>refresh</i> токен с помощью <i>access</i> токена будет ошибка
     */
    @Test
    void refreshAccessTokenWrongTokenType() {
        JwtAuthenticationDto tokens = jwtService.generateAuthToken(USERNAME);

        Throwable throwable = catchThrowable(
                () -> jwtService.refreshAccessToken(tokens.getAccessToken())
        );

        assertThat(throwable)
                .isInstanceOf(CustomJwtException.class)
                .extracting(ERROR_TYPE)
                .isEqualTo(ErrorType.UNEXPECTED_TOKEN_TYPE);
    }

    /**
     * Метод <b>validateJwtTokenInvalid</b> проверяет, что метод <b>validateJwtToken</b> возвращает <i>false</i> при передаче ему невалидного токена
     */
    @Test
    void validateJwtTokenInvalid() {
        boolean actual = jwtService.validateJwtToken(BROKEN_JWT_TOKEN);
        assertThat(actual).isFalse();
    }

}