package pet.project.shulzhenko.crud.integration.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import pet.project.shulzhenko.crud.dto.request.OrderDtoRequest;
import pet.project.shulzhenko.crud.dto.request.UpdateStatusDtoRequest;
import pet.project.shulzhenko.crud.entity.Status;
import pet.project.shulzhenko.crud.integration.ControllerIntegrationTest;
import pet.project.shulzhenko.crud.utils.ErrorType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("OrderController integration tests")
class OrderControllerImplIT extends ControllerIntegrationTest {

    private static final String API_ORDERS_URL = "/api/orders";
    private static final String API_ORDERS_ALL_URL = "/api/orders/all";

    /**
     * Проверяет успешное создание нового поста для пользователя user1.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 201 CREATED</li>
     *     <li>Формат ответа → JSON</li>
     *     <li>JSON не пустой</li>
     *     <li>ID у поста существует</li>
     *     <li>Описание совпадает с ожидаем, заранее созданным, значением</li>
     *     <li>Статус заказа CREATED</li>
     * </ul>
     */
    @Test
    @WithUserDetails(
            value = "user1",
            userDetailsServiceBeanName = "customUserServiceImpl"
    )
    @DisplayName("POST /api/orders → 201 CREATED (user creates order)")
    void createOrderShouldReturn201() throws Exception {

        OrderDtoRequest request = new OrderDtoRequest("test data");

        mockMvc.perform(post(API_ORDERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").isNotEmpty())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.status").value(Status.CREATED.name()));
    }

    /**
     * Проверяет ошибку создание поста при некорректных данных.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 400 Bad Request</li>
     *     <li>Возвращается объект ошибки валидации</li>
     * </ul>
     */
    @Test
    @WithUserDetails(
            value = "user1",
            userDetailsServiceBeanName = "customUserServiceImpl"
    )
    @DisplayName("POST /api/orders → 400 BAD_REQUEST (incorrect request data)")
    void createOrderIncorrectRequestDataShouldReturn400() throws Exception {

        OrderDtoRequest request = new OrderDtoRequest(null);

        mockMvc.perform(post(API_ORDERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorType.VALIDATION_FAILED.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.VALIDATION_FAILED.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_ORDERS_URL))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Проверяет ошибку при создании поста пользователем, не вошедшим в систему.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 401 Unauthorized</li>
     *     <li>Ошибка аутентификации</li>
     * </ul>
     */
    @Test
    @DisplayName("POST /api/orders → 401 UNAUTHORIZED (The user is not logged in)")
    void createOrderValidationFailedShouldReturn401() throws Exception {
        mockMvc.perform(post(API_ORDERS_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorType.JWT_VALIDATION_FAILED.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.JWT_VALIDATION_FAILED.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_ORDERS_URL))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Проверяет успешное получение списка постов для пользователя user1.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 200 OK</li>
     *     <li>Формат ответа → JSON</li>
     *     <li>Контент приходит в виде списка</li>
     *     <li>Список контента не пуст</li>
     *     <li>ID у пользователя существует</li>
     *     <li>ID у поста/постов существует</li>
     *     <li>Время создания поста существует</li>
     *     <li>Текущая страница пагинации существует</li>
     *     <li>Общее кол-во страниц существует</li>
     * </ul>
     */
    @Test
    @WithUserDetails(
            value = "user1",
            userDetailsServiceBeanName = "customUserServiceImpl"
    )
    @DisplayName("GET /api/orders → 200 OK (user gets own orders)")
    void userOrdersListShouldReturn200() throws Exception {
        mockMvc.perform(get(API_ORDERS_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").isNotEmpty())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].userId").exists())
                .andExpect(jsonPath("$.content[0].description").exists())
                .andExpect(jsonPath("$.content[0].status").exists())
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    /**
     * Проверяет ошибку при получении списка постов пользователя пользователем, не вошедшим в систему.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 401 Unauthorized</li>
     *     <li>Ошибка аутентификации</li>
     * </ul>
     */
    @Test
    @DisplayName("POST /api/orders → 401 UNAUTHORIZED (The user is not logged in)")
    void userOrdersListValidationFailedShouldReturn401() throws Exception {
        mockMvc.perform(post(API_ORDERS_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorType.JWT_VALIDATION_FAILED.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.JWT_VALIDATION_FAILED.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_ORDERS_URL))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Проверяет успешное получение списка все постов всех пользователей.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 200 OK</li>
     *     <li>Формат ответа → JSON</li>
     *     <li>Контент приходит в виде списка</li>
     *     <li>Список контента не пуст</li>
     *     <li>ID у пользователя существует</li>
     *     <li>ID у поста/постов существует</li>
     *     <li>Время создания поста существует</li>
     *     <li>Текущая страница пагинации существует</li>
     *     <li>Общее кол-во страниц существует</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("GET /api/orders/all → 200 OK (get all posts)")
    void getAllOrdersShouldReturn200() throws Exception {
        mockMvc.perform(get(API_ORDERS_ALL_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").isNotEmpty())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].userId").exists())
                .andExpect(jsonPath("$.content[0].description").exists())
                .andExpect(jsonPath("$.content[0].status").exists())
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    /**
     * Проверяет ошибку при получении списка всех постов пользователем, не вошедшим в систему.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 401 Unauthorized</li>
     *     <li>Ошибка аутентификации</li>
     * </ul>
     */
    @Test
    @DisplayName("GET /api/orders/all → 401 UNAUTHORIZED (The user is not ADMIN)")
    void getAllOrdersValidationFailedShouldReturn401() throws Exception {
        mockMvc.perform(post(API_ORDERS_ALL_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorType.JWT_VALIDATION_FAILED.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.JWT_VALIDATION_FAILED.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_ORDERS_ALL_URL))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * USER пользователь не имеет доступа к получению списка постов.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 403 FORBIDDEN</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "USER")
    @DisplayName("GET /api/orders/all → 403 FORBIDDEN (The user is not ADMIN)")
    void getAllOrdersShouldReturn403() throws Exception {
        mockMvc.perform(get(API_ORDERS_ALL_URL))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorType.NO_PERMISSION.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.NO_PERMISSION.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_ORDERS_ALL_URL))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Проверяет успешное обновление статуса заказа пользователем с ролью ADMIN.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 200 OK</li>
     *     <li>Формат ответа → JSON</li>
     *     <li>ID у поста соответствует ожидаемому</li>
     *     <li>ID у поста существует</li>
     *     <li>Описание у поста существует</li>
     *     <li>Статус заказа соответствует ожидаемому 'IN_PROGRESS'</li>
     *     <li>Время создания поста существует</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("PUT /api/orders/{id} → 200 (Status updated)")
    void updateStatusOrderShouldReturn200() throws Exception {
        UUID orderId = UUID.fromString("a1111111-1111-1111-1111-111111111111");
        UpdateStatusDtoRequest updateStatusDtoRequest = new UpdateStatusDtoRequest(Status.IN_PROGRESS);

        mockMvc.perform(put(API_ORDERS_URL + "/" + orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateStatusDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.status").value(Status.IN_PROGRESS.name()))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    /**
     * Проверяет ошибку при обновлении статуса заказа пользователем, не вошедшим в систему.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 401 Unauthorized</li>
     *     <li>Ошибка аутентификации</li>
     * </ul>
     */
    @Test
    @DisplayName("PUT /api/orders/{id} → 401 UNAUTHORIZED (The user is not ADMIN)")
    void updateStatusOrderShouldReturn401() throws Exception {
        UUID userId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        mockMvc.perform(put(API_ORDERS_URL + "/" + userId))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorType.JWT_VALIDATION_FAILED.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.JWT_VALIDATION_FAILED.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_ORDERS_URL + "/" + userId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * USER пользователь не имеет доступа к обновлению статуса заказа.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 403 FORBIDDEN</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "USER")
    @DisplayName("PUT /api/orders/{id} → 403 FORBIDDEN (The user is not ADMIN)")
    void updateStatusOrderShouldReturn403() throws Exception {
        UUID userId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        mockMvc.perform(put(API_ORDERS_URL + "/" + userId))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorType.NO_PERMISSION.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.NO_PERMISSION.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_ORDERS_URL + "/" + userId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * ADMIN получает ошибку при попытке обновить статус несуществующего заказа.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 404 NOT FOUND</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("PUT /api/orders/{id} → 404 NOT_FOUND (orderId is not found)")
    void updateStatusOrderShouldReturn404() throws Exception {
        UUID orderIdNotFound = UUID.randomUUID();
        UpdateStatusDtoRequest updateStatusDtoRequest = new UpdateStatusDtoRequest(Status.IN_PROGRESS);

        mockMvc.perform(put(API_ORDERS_URL + "/" + orderIdNotFound)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateStatusDtoRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorType.NO_SUCH_ORDER_BY_UUID.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.NO_SUCH_ORDER_BY_UUID.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_ORDERS_URL + "/" + orderIdNotFound))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Успешное удаление заказа пользователем с ролью ADMIN.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 204 SUCCESSFUL</li>
     *     <li>HTTP статус является 2ХХ</li>
     *     <li>В ответе нет контента</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("DELETE /api/orders/{id} → 204 SUCCESSFUL (delete order by ID)")
    void deleteOrderShouldReturn204() throws Exception {
        UUID orderId = UUID.fromString("a1111111-1111-1111-1111-111111111113");

        mockMvc.perform(delete(API_ORDERS_URL + "/" + orderId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(status().isNoContent());
    }

    /**
     * Проверяет ошибку при удалении заказа пользователем, не вошедшим в систему.
     *
     * <p><b>Ожидаемое поведение:</b></p>
     * <ul>
     *     <li>HTTP 401 Unauthorized</li>
     *     <li>Ошибка аутентификации</li>
     * </ul>
     */
    @Test
    @DisplayName("DELETE /api/orders/{id} → 401 UNAUTHORIZED (The user is not ADMIN)")
    void deleteOrderShouldReturn401() throws Exception {
        UUID userId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        mockMvc.perform(put(API_ORDERS_URL + "/" + userId))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorType.JWT_VALIDATION_FAILED.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.JWT_VALIDATION_FAILED.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_ORDERS_URL + "/" + userId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * USER пользователь не имеет доступа к удалению заказа или это не его заказ.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 403 FORBIDDEN</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "USER")
    @DisplayName("DELETE /api/orders/{id} → 403 FORBIDDEN (The user is not ADMIN)")
    void deleteOrderShouldReturn403() throws Exception {
        UUID userId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        mockMvc.perform(put(API_ORDERS_URL + "/" + userId))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorType.NO_PERMISSION.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.NO_PERMISSION.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_ORDERS_URL + "/" + userId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * ADMIN получает ошибку при попытке удалить несуществующий заказа.
     *
     * <p>
     * Ожидается:
     * <ul>
     *     <li>HTTP 404 NOT FOUND</li>
     * </ul>
     */
    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("DELETE /api/orders/{id} → 404 NOT_FOUND (orderId is not found)")
    void deleteOrderShouldReturn404() throws Exception {
        UUID orderIdNotFound = UUID.randomUUID();
        UpdateStatusDtoRequest updateStatusDtoRequest = new UpdateStatusDtoRequest(Status.IN_PROGRESS);

        mockMvc.perform(put(API_ORDERS_URL + "/" + orderIdNotFound)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateStatusDtoRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorType.NO_SUCH_ORDER_BY_UUID.name()))
                .andExpect(jsonPath("$.status").value(ErrorType.NO_SUCH_ORDER_BY_UUID.getStatus().value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(API_ORDERS_URL + "/" + orderIdNotFound))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}