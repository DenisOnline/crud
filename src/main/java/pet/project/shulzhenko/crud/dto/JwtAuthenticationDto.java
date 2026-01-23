package pet.project.shulzhenko.crud.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи JWT-токенов (access и refresh).
 *
 * <p>Используется как ответ при успешной аутентификации или обновлении токена.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationDto {

    /**
     * accessToken — токен доступа (обязательное поле)
     */
    @NotBlank
    private String accessToken;

    /**
     * refreshToken — токен обновления (обязательное поле)
     */
    @NotBlank
    private String refreshToken;

}