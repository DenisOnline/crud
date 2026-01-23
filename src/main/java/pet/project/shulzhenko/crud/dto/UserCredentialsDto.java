package pet.project.shulzhenko.crud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи учетных данных пользователя.
 *
 * <p>Используется при регистрации и аутентификации пользователя.</p>
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>username — имя пользователя (обязательное, не более 50 символов)</li>
 *     <li>password — пароль пользователя (обязательный, от 3 до 64 символов)</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDto {

    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank
    @Size(min = 3, max = 64)
    private String password;

}