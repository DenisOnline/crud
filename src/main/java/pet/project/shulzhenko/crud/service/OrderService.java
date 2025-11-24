package pet.project.shulzhenko.crud.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pet.project.shulzhenko.crud.dto.request.OrderDtoRequest;
import pet.project.shulzhenko.crud.dto.request.UpdateStatusDtoRequest;
import pet.project.shulzhenko.crud.dto.response.OrderDtoResponse;
import pet.project.shulzhenko.crud.exeption.OrderException;
import pet.project.shulzhenko.crud.exeption.UserException;
import pet.project.shulzhenko.crud.security.CustomUserDetails;

import java.util.UUID;

public interface OrderService {

    /**
     * Создаёт новый заказ от имени аутентифицированного пользователя.
     *
     * @param orderDto данные для создания заказа
     * @param userDetails данные текущего пользователя
     * @return созданный заказ
     */
    OrderDtoResponse createOrder(OrderDtoRequest orderDto, CustomUserDetails userDetails);

    /**
     * Возвращает страницу заказов пользователя по его уникальному идентификатору.
     *
     * @param userId идентификатор пользователя
     * @param pageable параметры пагинации
     * @return страница заказов пользователя
     * @throws UserException если пользователь не найден
     */
    Page<OrderDtoResponse> userOrdersList(UUID userId, Pageable pageable) throws UserException;

    /**
     * Возвращает страницу всех заказов в системе.
     *
     * @param pageable параметры пагинации
     * @return страница заказов
     */
    Page<OrderDtoResponse> getAllOrders(Pageable pageable);

    /**
     * Обновляет статус заказа по его уникальному идентификатору.
     *
     * @param orderId идентификатор заказа
     * @param statusDto новый статус заказа
     * @return обновлённый заказ
     * @throws OrderException если заказ не найден
     */
    OrderDtoResponse updateStatusOrder(UUID orderId, UpdateStatusDtoRequest statusDto) throws OrderException;

    /**
     * Удаляет заказ по его уникальному идентификатору.
     *
     * @param orderId идентификатор заказа
     * @throws OrderException если заказ не найден
     */
    void deleteOrder(UUID orderId) throws OrderException;

    /**
     * Проверяет, является ли пользователь владельцем заказа.
     *
     * @param orderId идентификатор заказа
     * @param userDetails данные текущего пользователя
     * @return {@code true}, если пользователь владеет заказом,
     *         иначе {@code false}
     */
    boolean isOrderOwner(UUID orderId, CustomUserDetails userDetails);

}