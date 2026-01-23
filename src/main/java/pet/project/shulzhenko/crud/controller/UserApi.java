package pet.project.shulzhenko.crud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import pet.project.shulzhenko.crud.dto.response.ErrorResponse;
import pet.project.shulzhenko.crud.dto.response.UserShortDtoResponse;

import java.util.List;
import java.util.UUID;

/**
 * Документация эндпоинтов для UserController через Swagger
 */
public interface UserApi {

    @Operation(
            summary = "Список всех пользователей",
            description = "Endpoint возвращает список всех пользователей. Доступно только для пользователей с ролью ADMIN.",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Получен список всех пользователей",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = UserShortDtoResponse.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован, требуется авторизация",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                    "code": "JWT_VALIDATION_FAILED",
                                                    "message": "JWT validation failed",
                                                    "status": 401,
                                                    "path": "/api/users",
                                                    "timestamp": "2025-12-17T07:03:51.928750200Z"
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "У текущего пользователя нет прав на выполнение данного запроса",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                    "code": "NO_PERMISSION",
                                                    "message": "User does not have permission to perform this action",
                                                    "status": 403,
                                                    "path": "/api/orders/all",
                                                    "timestamp": "2025-12-17T11:49:30.289774400Z"
                                                }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<List<UserShortDtoResponse>> getAllUsers();

    @Operation(
            summary = "Удаление пользователя",
            description = "Endpoint удаляет пользователя по UUID. Доступно только для пользователей с ролью ADMIN.",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Пользователь успешно удален"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован, требуется авторизация",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                    "code": "JWT_VALIDATION_FAILED",
                                                    "message": "JWT validation failed",
                                                    "status": 401,
                                                    "path": "/api/users/{id}",
                                                    "timestamp": "2025-12-17T07:03:51.928750200Z"
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "У текущего пользователя нет прав на выполнение данного запроса",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                    "code": "NO_PERMISSION",
                                                    "message": "User does not have permission to perform this action",
                                                    "status": 403,
                                                    "path": "/api/users/{id}",
                                                    "timestamp": "2025-12-17T11:49:30.289774400Z"
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь с UUID не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                    "code": "NO_SUCH_USER_BY_UUID",
                                                    "message": "No such user by UUID: {}",
                                                    "status": 404,
                                                    "path": "/api/users/{id}",
                                                    "timestamp": "2025-12-17T11:49:30.289774400Z"
                                                }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteUserById(@PathVariable UUID id);
}