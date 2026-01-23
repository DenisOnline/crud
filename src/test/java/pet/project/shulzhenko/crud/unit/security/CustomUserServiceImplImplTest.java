package pet.project.shulzhenko.crud.unit.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pet.project.shulzhenko.crud.entity.Role;
import pet.project.shulzhenko.crud.entity.User;
import pet.project.shulzhenko.crud.exeption.UserException;
import pet.project.shulzhenko.crud.repository.UserRepository;
import pet.project.shulzhenko.crud.security.CustomUserDetails;
import pet.project.shulzhenko.crud.security.CustomUserServiceImpl;
import pet.project.shulzhenko.crud.utils.ErrorType;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserServiceImplImplTest {

    private static final String USERNAME = "Denis";
    public static final String PASSWORD_ENCODE = "passwordEncode";
    public static final String ERROR_TYPE = "errorType";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserServiceImpl customUserService;

    /**
     * Метод <b>loadUserByUsernameSuccess</b> проверяет, что при успешной загрузке пользователя возвращается объект <i>CustomUserDetails</i>.
     */
    @Test
    void loadUserByUsernameSuccess() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(USERNAME)
                .password(PASSWORD_ENCODE)
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        CustomUserDetails actual = customUserService.loadUserByUsername(USERNAME);

        assertThat(actual)
                .isNotNull();

        assertThat(actual.user())
                .extracting(
                        User::getId,
                        User::getUsername,
                        User::getRole
                )
                .containsExactly(
                        user.getId(),
                        USERNAME,
                        Role.USER
                );

        verify(userRepository).findByUsername(USERNAME);
    }

    /**
     * Метод <b>loadUserByUsernameUserNotFound</b> проверяет, что при попытке найти пользователя с несуществующим именем, выбрасывается исключение.
     */
    @Test
    void loadUserByUsernameUserNotFound() {
        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(
                () -> customUserService.loadUserByUsername(USERNAME)
        );

        assertThat(throwable)
                .isInstanceOf(UserException.class)
                .extracting(ERROR_TYPE)
                .isEqualTo(ErrorType.FAILED_RETRIEVE_USER_BY_USERNAME);

        verify(userRepository).findByUsername(USERNAME);
    }
}