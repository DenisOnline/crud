package pet.project.shulzhenko.crud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pet.project.shulzhenko.crud.dto.request.OrderDtoRequest;
import pet.project.shulzhenko.crud.dto.request.UpdateStatusDtoRequest;
import pet.project.shulzhenko.crud.dto.response.ErrorResponse;
import pet.project.shulzhenko.crud.dto.response.OrderDtoResponse;
import pet.project.shulzhenko.crud.security.CustomUserDetails;

import java.util.UUID;

/**
 * Документация эндпоинтов для OrderController через Swagger
 */
public interface OrderApi {

    @Operation(
            summary = "Создание заказа",
            description = "Endpoint создаёт заказ для текущего авторизованного пользователя. От пользователя требуется авторизация. Создать заказ может пользователь с любой ролью (USER, ADMIN)",
            security = @SecurityRequirement(name = "bearer-jwt"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания заказа",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderDtoRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Заказ создан успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderDtoResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные данные запроса",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                    "code": "VALIDATION_FAILED",
                                                    "message": "description: не должно быть пустым",
                                                    "status": 400,
                                                    "path": "/api/orders",
                                                    "timestamp": "2025-12-17T07:03:51.928750200Z"
                                                }
                                            """)
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
                                                    "path": "/api/orders",
                                                    "timestamp": "2025-12-17T07:03:51.928750200Z"
                                                }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<OrderDtoResponse> creatingOrder(
            @Valid @RequestBody OrderDtoRequest orderDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "Список заказов текущего пользователя",
            description = "Endpoint возвращает страницу заказов текущего авторизованного пользователя",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список заказов текущего пользователя получен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderDtoResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                    "content": [
                                                        {
                                                            "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                                            "": "3fa85f64-5717-4562-b3fc-2c963f66afa7",
                                                            "description": "string",
                                                            "status": "CREATED",
                                                            "createdAt": "2025-12-15T11:19:25.262079"
                                                        }
                                                    ],
                                                    "pageable": {
                                                        "pageNumber": 0,
                                                        "pageSize": 1000,
                                                        "sort": {
                                                            "empty": true,
                                                            "sorted": false,
                                                            "unsorted": true
                                                        },
                                                        "offset": 0,
                                                        "paged": true,
                                                        "unpaged": false
                                                    },
                                                    "totalElements": 28,
                                                    "totalPages": 1,
                                                    "last": true,
                                                    "size": 1000,
                                                    "number": 0,
                                                    "sort": {
                                                        "empty": true,
                                                        "sorted": false,
                                                        "unsorted": true
                                                    },
                                                    "first": true,
                                                    "numberOfElements": 28,
                                                    "empty": false
                                                }
                                            """)
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
                                                    "path": "/api/orders",
                                                    "timestamp": "2025-12-17T07:03:51.928750200Z"
                                                }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<Page<OrderDtoResponse>> userOrdersList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "список всех заказов",
            description = "Endpoint возвращает страницу всех заказов от всех пользователей. Доступно пользователю с ролью ADMIN",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список всех заказов получен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderDtoResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                    "content": [
                                                        {
                                                            "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                                            "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa7",
                                                            "description": "string",
                                                            "status": "CREATED",
                                                            "createdAt": "2025-12-15T11:19:25.262079"
                                                        }
                                                    ],
                                                    "pageable": {
                                                        "pageNumber": 0,
                                                        "pageSize": 1000,
                                                        "sort": {
                                                            "empty": true,
                                                            "sorted": false,
                                                            "unsorted": true
                                                        },
                                                        "offset": 0,
                                                        "paged": true,
                                                        "unpaged": false
                                                    },
                                                    "totalElements": 28,
                                                    "totalPages": 1,
                                                    "last": true,
                                                    "size": 1000,
                                                    "number": 0,
                                                    "sort": {
                                                        "empty": true,
                                                        "sorted": false,
                                                        "unsorted": true
                                                    },
                                                    "first": true,
                                                    "numberOfElements": 28,
                                                    "empty": false
                                                }
                                            """)
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
                                                    "path": "/api/orders/all",
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
    ResponseEntity<Page<OrderDtoResponse>> getAllOrders(@ParameterObject Pageable pageable);

    @Operation(
            summary = "Обновление статуса заказа",
            description = "Endpoint изменяет статус заказа по UUID. Доступно только пользователю с ролью ADMIN",
            security = @SecurityRequirement(name = "bearer-jwt"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления статуса заказа",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateStatusDtoRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статус заказа обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderDtoResponse.class)
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
                                                    "path": "/api/orders/{id}",
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
                                                    "path": "/api/orders/{id}",
                                                    "timestamp": "2025-12-17T11:49:30.289774400Z"
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Заказ не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                     "code": "NO_SUCH_ORDER_BY_UUID",
                                                     "message": "No such order by UUID: {id}",
                                                     "status": 404,
                                                     "path": "/api/orders/{id}",
                                                     "timestamp": "2025-12-17T11:55:50.228065300Z"
                                                 }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<OrderDtoResponse> updateStatusOrder(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusDtoRequest status
    );

    @Operation(
            summary = "Удаление заказа",
            description = "Endpoint удаляет заказ по UUID. Доступно пользователю с ролью ADMIN или владельцу заказа",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Заказ удален"),
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
                                                    "path": "/api/orders/{id}",
                                                    "timestamp": "2025-12-17T07:03:51.928750200Z"
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "У текущего пользователя нет прав на выполнение данного запроса или он не является владельцем заказа",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                    "code": "NO_PERMISSION",
                                                    "message": "User does not have permission to perform this action",
                                                    "status": 403,
                                                    "path": "/api/orders/{id}",
                                                    "timestamp": "2025-12-17T11:49:30.289774400Z"
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Заказ не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                     "code": "NO_SUCH_ORDER_BY_UUID",
                                                     "message": "No such order by UUID: {id}",
                                                     "status": 404,
                                                     "path": "/api/orders/{id}",
                                                     "timestamp": "2025-12-17T11:55:50.228065300Z"
                                                 }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteOrder(@PathVariable UUID id);
}