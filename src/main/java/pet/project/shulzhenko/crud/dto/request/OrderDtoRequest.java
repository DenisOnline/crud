package pet.project.shulzhenko.crud.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания нового заказа.
 *
 * <p>Используется в API при создании заказа пользователем.</p>
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>description — описание заказа (обязательное поле, максимум 500 символов)</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDtoRequest {
    @NotBlank
    @Size(max = 500)
    String description;
}