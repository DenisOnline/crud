package pet.project.shulzhenko.crud.unit.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pet.project.shulzhenko.crud.dto.UserCredentialsDto;
import pet.project.shulzhenko.crud.dto.response.UserShortDtoResponse;
import pet.project.shulzhenko.crud.entity.Role;
import pet.project.shulzhenko.crud.entity.User;
import pet.project.shulzhenko.crud.exeption.UserException;
import pet.project.shulzhenko.crud.mapper.UserMapper;
import pet.project.shulzhenko.crud.repository.UserRepository;
import pet.project.shulzhenko.crud.service.impl.UserServiceImpl;
import pet.project.shulzhenko.crud.utils.ErrorType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    public static final String USERNAME_1 = "Denis";
    public static final String USERNAME_2 = "Vlad";
    public static final String PASSWORD = "password";
    public static final String PASSWORD_ENCODE = "passwordEncode";
    public static final String ERROR_TYPE = "errorType";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Проверка метода <b>createUser</b> на успешное создание пользователя
     */
    @Test
    void createUserSuccess() {
        var userCredentialsDto = new UserCredentialsDto(USERNAME_1, PASSWORD);

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(USERNAME_1)
                .password(PASSWORD)
                .build();

        var userShortDtoResponse = new UserShortDtoResponse(user.getId(), user.getUsername(), Role.USER);

        when(userRepository.existsByUsername(userCredentialsDto.getUsername())).thenReturn(false);
        when(userMapper.toEntity(userCredentialsDto)).thenReturn(user);
        when(passwordEncoder.encode(userCredentialsDto.getPassword())).thenReturn(PASSWORD_ENCODE);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toShortDto(user)).thenReturn(userShortDtoResponse);

        var actual = userService.createUser(userCredentialsDto);

        assertThat(actual)
                .isNotNull()
                .extracting(
                        UserShortDtoResponse::getId,
                        UserShortDtoResponse::getUsername,
                        UserShortDtoResponse::getRole
                )
                .containsExactly(
                        user.getId(),
                        user.getUsername(),
                        Role.USER
                );

        assertThat(user)
                .extracting(User::getRole, User::getPassword)
                .containsExactly(Role.USER, PASSWORD_ENCODE);

        verify(userRepository).existsByUsername(userCredentialsDto.getUsername());
        verify(userMapper).toEntity(userCredentialsDto);
        verify(passwordEncoder).encode(userCredentialsDto.getPassword());
        verify(userRepository).save(user);
        verify(userMapper).toShortDto(user);
    }

    /**
     * Проверка метода <b>createUser</b> на попытку создания пользователя с существующим <i>username</i>
     */
    @Test
    void createUserUsernameAlreadyExists() {
        var userCredentialsDto = new UserCredentialsDto(USERNAME_1, PASSWORD);

        when(userRepository.existsByUsername(userCredentialsDto.getUsername())).thenReturn(true);

        Throwable thrown = catchThrowable(
                () -> userService.createUser(userCredentialsDto)
        );

        assertThat(thrown)
                .isInstanceOf(UserException.class)
                .extracting(ERROR_TYPE)
                .isEqualTo(ErrorType.USERNAME_ALREADY_EXISTS);

        verify(userRepository).existsByUsername(userCredentialsDto.getUsername());
        verify(userMapper, never()).toEntity(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toShortDto(any());
    }


    /**
     * Проверка метода <b>findAllUsers</b> на успешное получение списка пользователей
     */
    @Test
    void findAllUsersSuccess() {
        User user1 = User.builder()
                .id(UUID.randomUUID())
                .username(USERNAME_1)
                .password(PASSWORD)
                .build();

        User user2 = User.builder()
                .id(UUID.randomUUID())
                .username(USERNAME_2)
                .password(PASSWORD)
                .build();

        var userShortDtoResponse1 = new UserShortDtoResponse(user1.getId(), user1.getUsername(), Role.USER);
        var userShortDtoResponse2 = new UserShortDtoResponse(user2.getId(), user2.getUsername(), Role.USER);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toShortDto(user1)).thenReturn(userShortDtoResponse1);
        when(userMapper.toShortDto(user2)).thenReturn(userShortDtoResponse2);

        var actual = userService.findAllUsers();

        assertThat(actual)
                .isNotEmpty()
                .hasSize(2)
                .extracting(UserShortDtoResponse::getUsername)
                .containsExactly(USERNAME_1, USERNAME_2);

        verify(userRepository).findAll();
        verify(userMapper).toShortDto(user1);
        verify(userMapper).toShortDto(user2);
    }

    /**
     * Проверка метода <b>findAllUsers</b> на пустой список пользователей
     */
    @Test
    void findAllUsers_empty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        var actual = userService.findAllUsers();

        assertThat(actual)
                .isNotNull()
                .isEmpty();

        verify(userRepository).findAll();
        verify(userMapper, never()).toShortDto(any());
    }

    /**
     * Проверка метода <b>deleteUserById</b> на успешное удаление пользователя
     */
    @Test
    void deleteUserById_success() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username(USERNAME_1)
                .password(PASSWORD)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    /**
     * Проверка метода <b>deleteUserById</b> на попытку удаления несуществующего пользователя
     */
    @Test
    void deleteUserById_userNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(
                () -> userService.deleteUserById(userId)
        );

        assertThat(throwable)
                .isInstanceOf(UserException.class)
                .extracting(ERROR_TYPE)
                .isEqualTo(ErrorType.NO_SUCH_USER_BY_UUID);

        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any());
    }
}