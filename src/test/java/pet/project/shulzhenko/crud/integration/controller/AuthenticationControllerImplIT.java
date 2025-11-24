package pet.project.shulzhenko.crud.integration.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pet.project.shulzhenko.crud.dto.RefreshTokenDto;
import pet.project.shulzhenko.crud.dto.UserCredentialsDto;
import pet.project.shulzhenko.crud.entity.Role;
import pet.project.shulzhenko.crud.integration.ControllerIntegrationTest;
import pet.project.shulzhenko.crud.utils.ErrorType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("AuthenticationController integration tests")
class AuthenticationControllerImplIT extends ControllerIntegrationTest {

    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String ADMIN = "admin1";
    public static final String NEW_USER = "new_user";
    public static final String EMPTY_USERNAME = "";
    public static final String PASSWORD = "123";
    public static final String NEW_PASSWORD = "password123";
    public static final String EMPTY_PASSWORD = "";
    public static final String WRONG_PASSWORD = "wrong_password";
    public static final String API_AUTH_REGISTER = "/api/auth/register";
    public static final String API_AUTH_LOGIN = "/api/auth/login";
    public static final String API_AUTH_ME = "/api/auth/me";
    public static final String API_AUTH_REFRESH = "/api/auth/refresh";

    /**
     * Проверяет успешную регистрацию нового пользователя.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 200 OK</li>
     *     <li>Пользователь создаётся в БД</li>
     *     <li>Возвращается краткая информация о пользователе</li>
     * </ul>
     */
    @Test
    @DisplayName("POST /api/auth/register → 200 OK (successful registration)")
    void registerShouldRegisterUserSuccessfully() throws Exception {
        UserCredentialsDto dto = new UserCredentialsDto(NEW_USER, NEW_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(NEW_USER))
                .andExpect(jsonPath("$.role").value(Role.USER.name()));
    }

    /**
     * Проверяет ошибку регистрации при некорректных данных.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 400 Bad Request</li>
     *     <li>Возвращается объект ошибки валидации</li>
     * </ul>
     */
    @Test
    @DisplayName("POST /api/auth/register → 400 Bad Request (validation failed)")
    void registerShouldFailOnInvalidData() throws Exception {
        UserCredentialsDto dto = new UserCredentialsDto(EMPTY_USERNAME, EMPTY_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorType.VALIDATION_FAILED.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.VALIDATION_FAILED.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_AUTH_REGISTER))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Проверяет успешный вход пользователя в систему.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 200 OK</li>
     *     <li>Возвращается accessToken и refreshToken</li>
     * </ul>
     */
    @Test
    @DisplayName("POST /api/auth/login → 200 OK (successful login)")
    void loginShouldReturnJwtTokens() throws Exception {
        UserCredentialsDto dto = new UserCredentialsDto(ADMIN, PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty()); //TODO: Добавить ниже 400 ошибку
    }

    /**
     * Проверяет ошибку входа при неверных учетных данных.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 401 Unauthorized</li>
     *     <li>Ошибка аутентификации</li>
     * </ul>
     */
    @Test
    @DisplayName("POST /api/auth/login → 401 Unauthorized (wrong credentials)")
    void loginShouldFailOnWrongCredentials() throws Exception {
        UserCredentialsDto dto = new UserCredentialsDto(ADMIN, WRONG_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code")
                        .value(ErrorType.USERNAME_OR_PASSWORD_IS_NOT_CORRECT.getMessage()));
    }

    /**
     * Проверяет получение информации о текущем пользователе.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 200 OK</li>
     *     <li>Возвращается полная информация о пользователе</li>
     * </ul>
     */
    @Test
    @DisplayName("GET /api/auth/me → 200 OK (authorized user)")
    void meShouldReturnCurrentUserInfo() throws Exception {
        String accessToken = obtainAccessToken(ADMIN, PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.get(API_AUTH_ME)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(ADMIN))
                .andExpect(jsonPath("$.role").value(Role.ADMIN.name()));//TODO: Добавить ниже 401 ошибку
    }

    /**
     * Проверяет обновление accessToken по refreshToken.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 200 OK</li>
     *     <li>Возвращается новый accessToken</li>
     * </ul>
     */
    @Test
    @DisplayName("POST /api/auth/refresh → 200 OK (refresh token)")
    void refreshShouldReturnNewAccessToken() throws Exception {
        String refreshToken = obtainRefreshToken(ADMIN, PASSWORD);

        RefreshTokenDto dto = new RefreshTokenDto(refreshToken);

        mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_REFRESH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());//TODO: Добавить ниже 401 ошибку
    }

    /**
     * Вспомогательный метод для получения <i>accessToken</i> по логину и паролю.
     */
    private String obtainAccessToken(String username, String password) throws Exception {
        UserCredentialsDto dto = new UserCredentialsDto(username, password);

        String response = mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get(ACCESS_TOKEN).asText();
    }

    /**
     * Вспомогательный метод для получения <i>refreshToken</i> по логину и паролю.
     */
    private String obtainRefreshToken(String username, String password) throws Exception {
        UserCredentialsDto dto = new UserCredentialsDto(username, password);

        String response = mockMvc.perform(MockMvcRequestBuilders.post(API_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get(REFRESH_TOKEN).asText();
    }
}