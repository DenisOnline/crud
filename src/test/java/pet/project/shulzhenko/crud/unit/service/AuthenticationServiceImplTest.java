package pet.project.shulzhenko.crud.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pet.project.shulzhenko.crud.dto.JwtAuthenticationDto;
import pet.project.shulzhenko.crud.dto.RefreshTokenDto;
import pet.project.shulzhenko.crud.dto.UserCredentialsDto;
import pet.project.shulzhenko.crud.dto.response.OrderDtoResponse;
import pet.project.shulzhenko.crud.dto.response.UserFullDtoResponse;
import pet.project.shulzhenko.crud.dto.response.UserShortDtoResponse;
import pet.project.shulzhenko.crud.entity.Role;
import pet.project.shulzhenko.crud.entity.Status;
import pet.project.shulzhenko.crud.entity.User;
import pet.project.shulzhenko.crud.exeption.CustomJwtException;
import pet.project.shulzhenko.crud.exeption.UserException;
import pet.project.shulzhenko.crud.mapper.UserMapper;
import pet.project.shulzhenko.crud.repository.UserRepository;
import pet.project.shulzhenko.crud.security.CustomUserDetails;
import pet.project.shulzhenko.crud.security.jwt.JwtService;
import pet.project.shulzhenko.crud.service.UserService;
import pet.project.shulzhenko.crud.service.impl.AuthenticationServiceImpl;
import pet.project.shulzhenko.crud.utils.ErrorType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    public static final String USERNAME = "Denis";
    public static final String PASSWORD = "password";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String ERROR_TYPE = "errorType";
    public static final String DESCRIPTION = "Order";

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;


    /**
     * Проверка метода <b>login</b> на успешную аутентификацию
     */
    @Test
    void loginSuccess() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        var userCredentialsDto = new UserCredentialsDto(USERNAME, PASSWORD);
        var optionalUser = Optional.of(user);
        var jwtAuthenticationDto = new JwtAuthenticationDto(ACCESS_TOKEN, REFRESH_TOKEN);

        when(userRepository.findByUsername(userCredentialsDto.getUsername())).thenReturn(optionalUser);
        when(passwordEncoder.matches(userCredentialsDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateAuthToken(user.getUsername())).thenReturn(jwtAuthenticationDto);

        var actual = authenticationService.login(userCredentialsDto);

        assertThat(actual)
                .isNotNull()
                .extracting(
                        JwtAuthenticationDto::getAccessToken,
                        JwtAuthenticationDto::getRefreshToken
                )
                .containsExactly(
                        jwtAuthenticationDto.getAccessToken(),
                        jwtAuthenticationDto.getRefreshToken()
                );

        verify(userRepository).findByUsername(userCredentialsDto.getUsername());
        verify(passwordEncoder).matches(userCredentialsDto.getPassword(), user.getPassword());
        verify(jwtService).generateAuthToken(user.getUsername());
    }

    /**
     * Проверка метода <b>login</b> на неуспешную аутентификацию при неверном логине
      */
    @Test
    void loginInvalidCredentialsUsername() {
        var userCredentialsDto = new UserCredentialsDto(USERNAME, PASSWORD);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(
                () -> authenticationService.login(userCredentialsDto)
        );

        assertThat(throwable)
                .isInstanceOf(UserException.class)
                .extracting(ERROR_TYPE)
                .isEqualTo(ErrorType.USERNAME_OR_PASSWORD_IS_NOT_CORRECT);

        verify(userRepository).findByUsername(USERNAME);
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateAuthToken(any());
    }

    /**
     * Проверка метода <b>login</b> на неуспешную аутентификацию при неверном пароле
     */
    @Test
    void loginInvalidCredentialsPassword() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        var userCredentialsDto = new UserCredentialsDto(USERNAME, PASSWORD);
        var optionalUser = Optional.of(user);

        when(userRepository.findByUsername(USERNAME)).thenReturn(optionalUser);
        when(passwordEncoder.matches(userCredentialsDto.getPassword(), user.getPassword())).thenReturn(false);

        Throwable throwable = catchThrowable(
                () -> authenticationService.login(userCredentialsDto)
        );

        assertThat(throwable)
                .isInstanceOf(UserException.class)
                .extracting(ERROR_TYPE)
                .isEqualTo(ErrorType.USERNAME_OR_PASSWORD_IS_NOT_CORRECT);

        verify(userRepository).findByUsername(USERNAME);
        verify(passwordEncoder).matches(userCredentialsDto.getPassword(), user.getPassword());
        verify(jwtService, never()).generateAuthToken(any());
    }

    /**
     * Проверка метода <b>registe</b>r на успешное создание пользователя через <i>userService</i>
     */
    @Test
    void registerSuccess() {
        var userCredentialsDto = new UserCredentialsDto(USERNAME, PASSWORD);
        var userShortDtoResponse = new UserShortDtoResponse(UUID.randomUUID(), USERNAME, Role.USER);

        when(userService.createUser(userCredentialsDto)).thenReturn(userShortDtoResponse);

        var actual = authenticationService.register(userCredentialsDto);

        assertThat(actual)
                .isNotNull()
                .extracting(
                        UserShortDtoResponse::getId,
                        UserShortDtoResponse::getUsername,
                        UserShortDtoResponse::getRole
                )
                .containsExactly(
                        userShortDtoResponse.getId(),
                        userShortDtoResponse.getUsername(),
                        userShortDtoResponse.getRole()
                );

        verify(userService).createUser(userCredentialsDto);
    }

    /**
     * Проверка метода <b>me</b> на успешное получение профиля
     */
    //me — успешное получение профиля
    @Test
    void meSuccess() {
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        LocalDateTime timeNow = LocalDateTime.now();

        User user = User.builder()
                .id(userId)
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        OrderDtoResponse order = OrderDtoResponse.builder()
                .id(orderId)
                .userId(userId)
                .description(DESCRIPTION)
                .status(Status.CREATED)
                .createdAt(timeNow)
                .build();

        var customUserDetails = new CustomUserDetails(user);
        var userFullDtoResponse = new UserFullDtoResponse(userId, USERNAME, Role.USER, List.of(order));

        when(userRepository.findByUsername(customUserDetails.getUsername())).thenReturn(Optional.of(user));
        when(userMapper.toFullDto(user)).thenReturn(userFullDtoResponse);

        var actual = authenticationService.me(customUserDetails);

        assertThat(actual)
                .isNotNull()
                .extracting(
                        UserFullDtoResponse::getId,
                        UserFullDtoResponse::getUsername,
                        UserFullDtoResponse::getRole,
                        UserFullDtoResponse::getOrders
                )
                .containsExactly(
                        userFullDtoResponse.getId(),
                        userFullDtoResponse.getUsername(),
                        userFullDtoResponse.getRole(),
                        userFullDtoResponse.getOrders()
                );

        verify(userRepository).findByUsername(customUserDetails.getUsername());
        verify(userMapper).toFullDto(user);
    }

    /**
     * Проверка метода <b>me</b>, если юзер по username не найден
     */
    @Test
    void meUserNotFound() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        var customUserDetails = new CustomUserDetails(user);

        when(userRepository.findByUsername(customUserDetails.getUsername())).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(
                () -> authenticationService.me(customUserDetails)
        );

        assertThat(throwable)
                .isInstanceOf(UserException.class)
                .extracting(ERROR_TYPE)
                .isEqualTo(ErrorType.NOT_FOUND_USERNAME);

        verify(userRepository).findByUsername(customUserDetails.getUsername());
        verify(userMapper, never()).toFullDto(any());
    }

    /**
     * Проверка метода <b>refreshToken</b> на успешное обновление токена
     */
    @Test
    void refreshTokenSuccess() {
        var refreshTokenDto = new RefreshTokenDto(REFRESH_TOKEN);
        var jwtAuthenticationDto = new JwtAuthenticationDto(ACCESS_TOKEN, REFRESH_TOKEN);

        when(jwtService.validateJwtToken(REFRESH_TOKEN)).thenReturn(true);
        when(jwtService.refreshAccessToken(REFRESH_TOKEN)).thenReturn(jwtAuthenticationDto);

        var actual = authenticationService.refreshToken(refreshTokenDto);

        assertThat(actual)
                .isNotNull()
                .extracting(
                        JwtAuthenticationDto::getAccessToken,
                        JwtAuthenticationDto::getRefreshToken
                )
                .containsExactly(
                        jwtAuthenticationDto.getAccessToken(),
                        jwtAuthenticationDto.getRefreshToken()
                );

        verify(jwtService).validateJwtToken(REFRESH_TOKEN);
        verify(jwtService).refreshAccessToken(REFRESH_TOKEN);
    }

    /**
     * Проверка метода <b>refreshToken</b> при провальной валидации токена
     */
    @Test
    void refreshTokenInvalidToken() {
        var refreshTokenDto = new RefreshTokenDto(REFRESH_TOKEN);

        when(jwtService.validateJwtToken(REFRESH_TOKEN)).thenReturn(false);

        Throwable throwable = catchThrowable(
                () -> authenticationService.refreshToken(refreshTokenDto)
        );

        assertThat(throwable)
                .isInstanceOf(CustomJwtException.class)
                .extracting(ERROR_TYPE)
                .isEqualTo(ErrorType.INVALID_REFRESH_TOKEN);

        verify(jwtService).validateJwtToken(REFRESH_TOKEN);
        verify(jwtService, never()).refreshAccessToken(any());
    }
}