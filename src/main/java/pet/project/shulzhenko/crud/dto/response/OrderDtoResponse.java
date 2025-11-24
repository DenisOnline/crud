package pet.project.shulzhenko.crud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pet.project.shulzhenko.crud.entity.Status;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для передачи информации о заказе.
 *
 * <p>Используется для ответа API при запросах информации о заказах.</p>
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>id — уникальный идентификатор заказа</li>
 *     <li>userId — идентификатор пользователя, которому принадлежит заказ</li>
 *     <li>description — описание заказа</li>
 *     <li>status — текущий статус заказа ({@link Status})</li>
 *     <li>createdAt — дата и время создания заказа</li>
 * </ul>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDtoResponse {
    private UUID id;

    private UUID userId;

    private String description;

    private Status status;

    private LocalDateTime createdAt;
}