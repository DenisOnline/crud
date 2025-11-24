package pet.project.shulzhenko.crud.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.project.shulzhenko.crud.dto.JwtAuthenticationDto;
import pet.project.shulzhenko.crud.dto.RefreshTokenDto;
import pet.project.shulzhenko.crud.dto.UserCredentialsDto;
import pet.project.shulzhenko.crud.dto.response.UserFullDtoResponse;
import pet.project.shulzhenko.crud.dto.response.UserShortDtoResponse;
import pet.project.shulzhenko.crud.entity.User;
import pet.project.shulzhenko.crud.exeption.CustomJwtException;
import pet.project.shulzhenko.crud.exeption.UserException;
import pet.project.shulzhenko.crud.mapper.UserMapper;
import pet.project.shulzhenko.crud.repository.UserRepository;
import pet.project.shulzhenko.crud.security.CustomUserDetails;
import pet.project.shulzhenko.crud.security.jwt.JwtService;
import pet.project.shulzhenko.crud.service.AuthenticationService;
import pet.project.shulzhenko.crud.service.UserService;
import pet.project.shulzhenko.crud.utils.ErrorType;
import pet.project.shulzhenko.crud.utils.LogType;

import java.util.Optional;

/**
 * Сервис, отвечающий за аутентификацию и управление JWT-токенами.
 *
 * <p>
 * Реализует:
 * <ul>
 *     <li>вход пользователя в систему</li>
 *     <li>регистрацию пользователей</li>
 *     <li>обновление access-токена</li>
 *     <li>получение профиля текущего пользователя</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    public JwtAuthenticationDto login(UserCredentialsDto userCredentialsDto)  {
        log.info(LogType.AUTH_LOGIN_ATTEMPT.getMessage(), userCredentialsDto.getUsername());
        User user = findByCredentials(userCredentialsDto);
        log.info(LogType.AUTH_LOGIN_SUCCESS.getMessage(), user.getId(), user.getUsername());
        return jwtService.generateAuthToken(user.getUsername());
    }

    @Override
    public UserShortDtoResponse register(UserCredentialsDto userCredentialsDto) {
        log.info(LogType.AUTH_REGISTER_ATTEMPT.getMessage(), userCredentialsDto.getUsername());
        return userService.createUser(userCredentialsDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserFullDtoResponse me(CustomUserDetails userDetails) {
        log.debug(LogType.AUTH_PROFILE_FETCH.getMessage(), userDetails.getUsername());
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserException(ErrorType.NOT_FOUND_USERNAME, userDetails.getUsername()));
        return userMapper.toFullDto(user);
    }

    @Override
    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) {
        log.info(LogType.AUTH_TOKEN_REFRESH.getMessage());
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (jwtService.validateJwtToken(refreshToken)) {
            return jwtService.refreshAccessToken(refreshToken);
        }
        throw new CustomJwtException(ErrorType.INVALID_REFRESH_TOKEN, refreshTokenDto);
    }

    /**
     * Проверяет учетные данные пользователя.
     *
     * @param userCredentialsDto DTO с логином и паролем
     * @return пользователь при успешной проверке
     * @throws UserException если логин или пароль неверны
     */
    private User findByCredentials(UserCredentialsDto userCredentialsDto) {
        Optional<User> optionalUser = userRepository.findByUsername(userCredentialsDto.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(userCredentialsDto.getPassword(), user.getPassword())) {
                return user;
            }
        }
        throw new UserException(ErrorType.USERNAME_OR_PASSWORD_IS_NOT_CORRECT);
    }
}