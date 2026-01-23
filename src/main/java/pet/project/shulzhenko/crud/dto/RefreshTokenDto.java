package pet.project.shulzhenko.crud.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи refresh-токена.
 *
 * <p>Используется для обновления access-токена при действительном refresh-токене.</p>
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>refreshToken — токен обновления (обязательное поле)</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {

    @NotBlank
    private String refreshToken;

}