package pet.project.shulzhenko.crud.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pet.project.shulzhenko.crud.dto.request.OrderDtoRequest;
import pet.project.shulzhenko.crud.dto.request.UpdateStatusDtoRequest;
import pet.project.shulzhenko.crud.dto.response.OrderDtoResponse;
import pet.project.shulzhenko.crud.entity.Order;
import pet.project.shulzhenko.crud.entity.Status;
import pet.project.shulzhenko.crud.entity.User;
import pet.project.shulzhenko.crud.exeption.OrderException;
import pet.project.shulzhenko.crud.exeption.UserException;
import pet.project.shulzhenko.crud.mapper.OrderMapper;
import pet.project.shulzhenko.crud.repository.OrderRepository;
import pet.project.shulzhenko.crud.repository.UserRepository;
import pet.project.shulzhenko.crud.security.CustomUserDetails;
import pet.project.shulzhenko.crud.service.impl.OrderServiceImpl;
import pet.project.shulzhenko.crud.utils.ErrorType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    public static final String DESCRIPTION = "New order";
    public static final String USERNAME = "Denis";
    public static final String PASSWORD = "password";
    public static final String ERROR_TYPE = "errorType";
    public static final String USERNAME_ANOTHER = "another";

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    UUID userId;
    UUID orderId;
    LocalDateTime timeNow;
    User user;
    Order order;
    CustomUserDetails customUserDetails;
    OrderDtoRequest orderDtoRequest;
    OrderDtoResponse orderDtoResponse;

    @BeforeEach
    void beforeEach() {
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        timeNow = LocalDateTime.now();

        user = User.builder()
                .id(userId)
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        order = Order.builder()
                .id(orderId)
                .user(user)
                .description(DESCRIPTION)
                .status(Status.CREATED)
                .createdAt(timeNow)
                .build();

        customUserDetails = new CustomUserDetails(user);
        orderDtoRequest = new OrderDtoRequest(DESCRIPTION);
        orderDtoResponse = new OrderDtoResponse(
                orderId,
                userId,
                order.getDescription(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    /**
     *
     * Проверка метода <b>createOrder</b> на успешное создание заказа
     */
    @Test
    void createOrderSuccess() {
        when(orderMapper.toEntity(orderDtoRequest)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderDtoResponse);

        var actual = orderService.createOrder(orderDtoRequest, customUserDetails);

        assertThat(actual)
                .isNotNull()
                .extracting(
                        OrderDtoResponse::getId,
                        OrderDtoResponse::getUserId,
                        OrderDtoResponse::getDescription,
                        OrderDtoResponse::getCreatedAt,
                        OrderDtoResponse::getStatus
                )
                .containsExactly(
                        orderDtoResponse.getId(),
                        orderDtoResponse.getUserId(),
                        orderDtoResponse.getDescription(),
                        orderDtoResponse.getCreatedAt(),
                        orderDtoResponse.getStatus()
                );

        assertThat(order)
                .extracting(
                        Order::getUser,
                        Order::getStatus
                )
                .containsExactly(
                        user,
                        Status.CREATED
                );

        verify(orderMapper).toEntity(orderDtoRequest);
        verify(orderRepository).save(order);
        verify(orderMapper).toDto(order);
    }


    /**
     * Проверка метода <b>userOrdersList</b> на успешное получение списка заказов пользователя
     */
    @Test
    void userOrdersListSuccess() {
        Pageable pageable = PageRequest.of(0, 2);
        Order order1 = Order.builder().id(UUID.randomUUID()).user(user).build();
        Order order2 = Order.builder().id(UUID.randomUUID()).user(user).build();

        Page<Order> orderPage = new PageImpl<>(
                List.of(order1, order2),
                pageable,
                5
        );

        OrderDtoResponse dto1 = new OrderDtoResponse(order1.getId(), order1.getUser().getId(), order1.getDescription(), order1.getStatus(), order1.getCreatedAt());
        OrderDtoResponse dto2 = new OrderDtoResponse(order2.getId(), order2.getUser().getId(), order2.getDescription(), order2.getStatus(), order2.getCreatedAt());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findAllByUser(user, pageable)).thenReturn(orderPage);
        when(orderMapper.toDto(order1)).thenReturn(dto1);
        when(orderMapper.toDto(order2)).thenReturn(dto2);

        Page<OrderDtoResponse> actual = orderService.userOrdersList(userId, pageable);

        assertThat(actual.getContent())
                .hasSize(2)
                .containsExactly(dto1, dto2);

        verify(userRepository).findById(userId);
        verify(orderRepository).findAllByUser(user, pageable);
        verify(orderMapper).toDto(order1);
        verify(orderMapper).toDto(order2);
    }

    /**
     * Проверка метода <b>userOrdersList</b> на ошибку при получении списка заказов пользователя, если пользователь не найден
     */
    @Test
    void userOrdersListUserNotFound() {
        Pageable pageable = PageRequest.of(0, 5);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(
                () -> orderService.userOrdersList(userId, pageable)
        );

        assertThat(throwable)
                .isInstanceOf(UserException.class)
                .extracting(ERROR_TYPE)
                .isEqualTo(ErrorType.NO_SUCH_USER_BY_UUID);

        verify(userRepository).findById(userId);
        verify(orderRepository, never()).findAllByUser(any(), any());
    }

    /**
     * Проверка метода <b>getAllOrders</b> на успешное получение списка всех заказов
     */
    @Test
    void getAllOrdersSuccess() {
        Pageable pageable = PageRequest.of(1, 3);
        Order order1 = Order.builder().id(UUID.randomUUID()).user(user).build();
        Order order2 = Order.builder().id(UUID.randomUUID()).user(user).build();
        Page<Order> orderPage = new PageImpl<>(
                List.of(order1, order2),
                pageable,
                5
        );
        OrderDtoResponse dto1 = new OrderDtoResponse(order1.getId(), order1.getUser().getId(), order1.getDescription(), order1.getStatus(), order1.getCreatedAt());
        OrderDtoResponse dto2 = new OrderDtoResponse(order2.getId(), order2.getUser().getId(), order2.getDescription(), order2.getStatus(), order2.getCreatedAt());

        when(orderRepository.findAllBy(pageable)).thenReturn(orderPage);
        when(orderMapper.toDto(order1)).thenReturn(dto1);
        when(orderMapper.toDto(order2)).thenReturn(dto2);

        Page<OrderDtoResponse> actual = orderService.getAllOrders(pageable);

        assertThat(actual.getContent())
                .hasSize(2)
                .containsExactly(dto1, dto2);

        verify(orderRepository).findAllBy(pageable);
        verify(orderMapper).toDto(order1);
        verify(orderMapper).toDto(order2);
    }

    /**
     * Проверка метода <b>updateStatusOrder</b> на успешное обновление статуса заказа
     */
    @Test
    void updateStatusOrderSuccess() {
        var updateStatusDtoRequest = new UpdateStatusDtoRequest(Status.IN_PROGRESS);
        orderDtoResponse.setStatus(Status.IN_PROGRESS);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderDtoResponse);

        var actual = orderService.updateStatusOrder(order.getId(), updateStatusDtoRequest);

        assertThat(actual.getStatus()).isEqualTo(Status.IN_PROGRESS);
        assertThat(order.getStatus()).isEqualTo(Status.IN_PROGRESS);

        verify(orderRepository).findById(order.getId());
        verify(orderRepository).save(order);
        verify(orderMapper).toDto(order);
    }

    /**
     * Проверка метода <b>updateStatusOrder</b> на ошибку при обновлении статуса заказа, если заказ не найден
     */
    @Test
    void updateStatusOrderOrderNotFound() {

        var updateStatusDtoRequest = new UpdateStatusDtoRequest(Status.IN_PROGRESS);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(
                () -> orderService.updateStatusOrder(order.getId(), updateStatusDtoRequest)
        );

        assertThat(throwable)
                .isInstanceOf(OrderException.class)
                .extracting(ERROR_TYPE)
                .isEqualTo(ErrorType.NO_SUCH_ORDER_BY_UUID);

        assertThat(order.getStatus()).isEqualTo(Status.CREATED);

        verify(orderRepository).findById(order.getId());
        verify(orderRepository, never()).save(any());
        verify(orderMapper, never()).toDto(any());
    }

    /**
     * Проверка метода <b>deleteOrder</b> на успешное удаление заказа
     */
    @Test
    void deleteOrderSuccess() {
        when(orderRepository.existsById(order.getId())).thenReturn(true);

        orderService.deleteOrder(order.getId());

        verify(orderRepository).existsById(order.getId());
        verify(orderRepository).deleteById(order.getId());
    }

    /**
     * Проверка метода <b>deleteOrder</b> на ошибку при удалении заказа, если заказ не найден
     */
    @Test
    void deleteOrderNotFound() {
        when(orderRepository.existsById(order.getId())).thenReturn(false);

        Throwable throwable = catchThrowable(
                () -> orderService.deleteOrder(order.getId())
        );

        assertThat(throwable)
                .isInstanceOf(OrderException.class)
                .extracting(ERROR_TYPE)
                .isEqualTo(ErrorType.NO_SUCH_ORDER_BY_UUID);

        verify(orderRepository).existsById(order.getId());
        verify(orderRepository, never()).deleteById(any());
    }

    /**
     * Проверка метода <b>isOrderOwner</b> на успешное сравнение пользователя и заказа
     */
    @Test
    void isOrderOwnerTrue() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        boolean actual = orderService.isOrderOwner(order.getId(), customUserDetails);

        assertThat(actual).isTrue();

        verify(orderRepository).findById(order.getId());
    }

    /**
     * Проверка метода <b>isOrderOwner</b> на <i>false</i>, когда пользователь не является владельцем заказа
     */
    @Test
    void isOrderOwnerFalse() {
        User anotherUser = User.builder()
                .id(UUID.randomUUID())
                .username(USERNAME_ANOTHER)
                .password(PASSWORD)
                .build();

        CustomUserDetails anotherUserDetails = new CustomUserDetails(anotherUser);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        boolean actual = orderService.isOrderOwner(order.getId(), anotherUserDetails);

        assertThat(actual).isFalse();

        verify(orderRepository).findById(order.getId());
    }

    /**
     * Проверка метода <b>isOrderOwner</b> на <i>false</i>, когда заказ не найден
     */
    @Test
    void isOrderOwnerOrderNotFound() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());

        boolean actual = orderService.isOrderOwner(order.getId(), customUserDetails);

        assertThat(actual).isFalse();

        verify(orderRepository).findById(order.getId());
    }

}
