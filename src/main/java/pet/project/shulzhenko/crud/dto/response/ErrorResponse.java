package pet.project.shulzhenko.crud.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * DTO для передачи информации об ошибках API.
 *
 * <p>Используется в глобальном обработчике исключений для формирования
 * стандартного ответа с деталями ошибки.</p>
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>code — код ошибки ({@link String})</li>
 *     <li>message — текстовое сообщение об ошибке</li>
 *     <li>status — HTTP-статус ошибки</li>
 *     <li>path — URI запроса, вызвавшего ошибку</li>
 *     <li>timestamp — время возникновения ошибки ({@link Instant})</li>
 * </ul>
 */
@Getter
@Builder
public class ErrorResponse {

    private final String code;
    private final String message;
    private final int status;
    private final String path;
    private final Instant timestamp;

}