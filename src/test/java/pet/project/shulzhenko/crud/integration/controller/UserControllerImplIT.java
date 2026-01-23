package pet.project.shulzhenko.crud.integration.controller;

import org.junit.jupiter.api.DisplayName;
import pet.project.shulzhenko.crud.integration.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import pet.project.shulzhenko.crud.utils.ErrorType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("UserController integration tests")
public class UserControllerImplIT extends ControllerIntegrationTest {

    private static final String API_USERS_URL = "/api/users";

    /**
     * ADMIN пользователь может получить список всех пользователей.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 200 OK</li>
     *     <li>Content-Type application/json</li>
     *     <li>В ответе присутствует список пользователей</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("GET /api/users → 200 OK (ADMIN can get all users)")
    void getAllUsersAsAdminShouldReturn200AndUsersList() throws Exception {
        mockMvc.perform(get(API_USERS_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").isNotEmpty())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].username").exists());
    }


    /**
     * Не аутентифицированный пользователь не может получить список пользователей.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 401 UNAUTHORIZED</li>
     * </ul>
     */
    @Test
    @DisplayName("GET /api/users → 401 UNAUTHORIZED (unauthenticated user cannot get users)")
    void getAllUsersWithoutAuthShouldReturn401() throws Exception {
        mockMvc.perform(get(API_USERS_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorType.JWT_VALIDATION_FAILED.getMessage()))
                .andExpect(jsonPath("$.status").value(ErrorType.JWT_VALIDATION_FAILED.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_USERS_URL))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * USER пользователь не имеет доступа к получению списка пользователей.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 403 FORBIDDEN</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "USER")
    @DisplayName("GET /api/users → 403 FORBIDDEN (USER cannot get all users)")
    void getAllUsersAsUserShouldReturn403() throws Exception {
        mockMvc.perform(get(API_USERS_URL))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorType.NO_PERMISSION.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.NO_PERMISSION.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_USERS_URL))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * ADMIN пользователь может удалить пользователя по UUID.
     *
     * <p>
     * Используется UUID пользователя, существующего в тестовой БД.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 204 NO CONTENT</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("DELETE /api/users/{id} → 204 NO CONTENT (ADMIN can delete user)")
    void deleteUserByIdAsAdminShouldReturn204() throws Exception {
        UUID userId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        mockMvc.perform(delete(API_USERS_URL + "/" + userId))
                .andExpect(status().isNoContent());
    }

    /**
     * USER пользователь не может удалять других пользователей.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 403 FORBIDDEN</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "USER")
    @DisplayName("DELETE /api/users/{id} → 403 FORBIDDEN (USER cannot delete user)")
    void deleteUserByIdAsUserShouldReturn403() throws Exception {
        UUID userId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        mockMvc.perform(delete(API_USERS_URL + "/" + userId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorType.NO_PERMISSION.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.NO_PERMISSION.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_USERS_URL + "/" + userId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * ADMIN получает ошибку при попытке удалить несуществующего пользователя.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 404 NOT FOUND</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("DELETE /api/users/{id} → 404 NOT FOUND (ADMIN tries to delete non-existing user)")
    void deleteUserByIdUserNotFoundShouldReturn404() throws Exception {
        UUID notExistingId = UUID.randomUUID();

        mockMvc.perform(delete(API_USERS_URL + "/" + notExistingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorType.NO_SUCH_USER_BY_UUID.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.NO_SUCH_USER_BY_UUID.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_USERS_URL + "/" + notExistingId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Не аутентифицированный пользователь не может удалить пользователя.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 401 UNAUTHORIZED</li>
     * </ul>
     */
    @Test
    @DisplayName("DELETE /api/users/{id} → 401 UNAUTHORIZED (unauthenticated user cannot delete user)")
    void deleteUserByIdWithoutAuthShouldReturn401() throws Exception {
        UUID userId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        mockMvc.perform(delete(API_USERS_URL + "/" + userId))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorType.JWT_VALIDATION_FAILED.getMessage()))
                .andExpect(jsonPath("$.status").value(ErrorType.JWT_VALIDATION_FAILED.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_USERS_URL + "/" + userId))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}