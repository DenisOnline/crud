package pet.project.shulzhenko.crud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import pet.project.shulzhenko.crud.dto.JwtAuthenticationDto;
import pet.project.shulzhenko.crud.dto.RefreshTokenDto;
import pet.project.shulzhenko.crud.dto.UserCredentialsDto;
import pet.project.shulzhenko.crud.dto.response.ErrorResponse;
import pet.project.shulzhenko.crud.dto.response.UserFullDtoResponse;
import pet.project.shulzhenko.crud.dto.response.UserShortDtoResponse;
import pet.project.shulzhenko.crud.security.CustomUserDetails;

/**
 * Документация эндпоинтов для AuthenticationController через Swagger
 */
public interface AuthenticationApi {

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Endpoint регистрации нового пользователя с ролью USER",
            security = @SecurityRequirement(name = "bearer-jwt"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для регистрации нового пользователя",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserCredentialsDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь успешно зарегистрирован",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserShortDtoResponse.class)
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
                                                     "message": "username: не должно быть пустым; username: размер должен находиться в диапазоне от 0 до 50; password: не должно быть пустым; password: размер должен находиться в диапазоне от 3 до 64",
                                                     "status": 400,
                                                     "path": "/api/auth/register",
                                                     "timestamp": "2025-12-17T13:10:53.695768300Z"
                                                 }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Такой пользователь уже существует",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                    "code": "USERNAME_ALREADY_EXISTS",
                                                    "message": "Username: {Username} already exists",
                                                    "status": 409,
                                                    "path": "/api/auth/register",
                                                    "timestamp": "2025-12-17T13:12:35.301289200Z"
                                                }
                                            """)
                            )
                    )
            }
    )
    UserShortDtoResponse register(@Valid @RequestBody UserCredentialsDto userCredentialsDto);

    @Operation(
            summary = "Вход пользователя в систему",
            description = "Endpoint для входа пользователя в систему",
            security = @SecurityRequirement(name = "bearer-jwt"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для входа пользователя в систему",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserCredentialsDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь успешно вошел в систему",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtAuthenticationDto.class)
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
                                                     "message": "username: не должно быть пустым; username: размер должен находиться в диапазоне от 0 до 50; password: не должно быть пустым; password: размер должен находиться в диапазоне от 3 до 64",
                                                     "status": 400,
                                                     "path": "/api/auth/login",
                                                     "timestamp": "2025-12-17T13:10:53.695768300Z"
                                                 }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неверный логин или пароль",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                    "code": "USERNAME_OR_PASSWORD_IS_NOT_CORRECT",
                                                    "message": "Username or password is not correct",
                                                    "status": 401,
                                                    "path": "/api/auth/login",
                                                    "timestamp": "2025-12-17T13:17:29.813977900Z"
                                                }
                                            """)
                            )
                    )
            }
    )
    JwtAuthenticationDto login(@Valid @RequestBody UserCredentialsDto userCredentialsDto);

    @Operation(
            summary = "Информация о текущем пользователе",
            description = "Endpoint предоставляет информацию о текущем пользователе. Доступен только авторизованным пользователям",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Получена информация о текущем пользователе",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserFullDtoResponse.class)
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
                                                    "path": "/api/auth/me",
                                                    "timestamp": "2025-12-17T07:03:51.928750200Z"
                                                }
                                            """)
                            )
                    )
            }
    )
    UserFullDtoResponse me(@AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(
            summary = "Обновление accessToken",
            description = "Endpoint для обновления accessToken",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "accessToken успешно обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtAuthenticationDto.class)
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
                                                    "path": "/api/auth/refresh",
                                                    "timestamp": "2025-12-17T07:03:51.928750200Z"
                                                }
                                            """)
                            )
                    )
            }
    )
    JwtAuthenticationDto refresh(@Valid @RequestBody RefreshTokenDto refreshTokenDto);
}