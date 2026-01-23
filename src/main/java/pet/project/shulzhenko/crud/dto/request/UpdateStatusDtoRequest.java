package pet.project.shulzhenko.crud.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pet.project.shulzhenko.crud.entity.Status;

/**
 * DTO для обновления статуса заказа.
 *
 * <p>Используется в API для изменения статуса существующего заказа.</p>
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>status — новый статус заказа ({@link Status}, обязательное поле)</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusDtoRequest {
    @NotNull
    Status status;
}