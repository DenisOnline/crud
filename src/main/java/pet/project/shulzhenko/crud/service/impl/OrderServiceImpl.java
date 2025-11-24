package pet.project.shulzhenko.crud.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.project.shulzhenko.crud.dto.request.OrderDtoRequest;
import pet.project.shulzhenko.crud.dto.request.UpdateStatusDtoRequest;
import pet.project.shulzhenko.crud.dto.response.OrderDtoResponse;
import pet.project.shulzhenko.crud.entity.Order;
import pet.project.shulzhenko.crud.entity.User;
import pet.project.shulzhenko.crud.exeption.OrderException;
import pet.project.shulzhenko.crud.exeption.UserException;
import pet.project.shulzhenko.crud.mapper.OrderMapper;
import pet.project.shulzhenko.crud.repository.OrderRepository;
import pet.project.shulzhenko.crud.repository.UserRepository;
import pet.project.shulzhenko.crud.security.CustomUserDetails;
import pet.project.shulzhenko.crud.service.OrderService;
import pet.project.shulzhenko.crud.utils.ErrorType;
import pet.project.shulzhenko.crud.utils.LogType;

import java.util.UUID;

/**
 * Сервисный слой, отвечающий за управление заказами.
 * <p>
 * Предоставляет операции:
 * <ul>
 *     <li>Создание заказов</li>
 *     <li>Получение страницу заказов пользователя</li>
 *     <li>Получение страницу всех заказов</li>
 *     <li>Обновление статуса заказа</li>
 *     <li>Удаление заказов</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional()
    public OrderDtoResponse createOrder(OrderDtoRequest orderDto, CustomUserDetails userDetails) {
        log.info(LogType.ORDER_CREATE_START.getMessage(), userDetails.user().getId());

        Order order = orderMapper.toEntity(orderDto);
        order.setUser(userDetails.user());
        orderRepository.save(order);

        log.info(LogType.ORDER_CREATED.getMessage(), order.getId(), userDetails.user().getId());
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDtoResponse> userOrdersList(UUID userId, Pageable pageable) {
        log.debug(LogType.ORDER_FETCH_BY_USER_START.getMessage(), userId, pageable.getPageNumber(), pageable.getPageSize());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorType.NO_SUCH_USER_BY_UUID, userId));

        Page<OrderDtoResponse> page = orderRepository
                .findAllByUser(user, pageable)
                .map(orderMapper::toDto);

        log.debug(LogType.ORDER_FETCH_BY_USER_RESULT.getMessage(), userId, page.getTotalElements());
        return page;
    }

    @Override
    public Page<OrderDtoResponse> getAllOrders(Pageable pageable) {
        log.debug(LogType.ORDER_FETCH_ALL_START.getMessage(), pageable.getPageNumber(), pageable.getPageSize());

        Page<OrderDtoResponse> page = orderRepository
                .findAllBy(pageable)
                .map(orderMapper::toDto);

        log.debug(LogType.ORDER_FETCH_ALL_RESULT.getMessage(), page.getTotalElements());
        return page;
    }

    @Override
    @Transactional
    public OrderDtoResponse updateStatusOrder(UUID orderId, UpdateStatusDtoRequest statusDto) {
        log.info(LogType.ORDER_STATUS_UPDATE_START.getMessage(), orderId, statusDto.getStatus());
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(statusDto.getStatus());
                    orderRepository.save(order);
                    log.info(LogType.ORDER_STATUS_UPDATED.getMessage(), order.getId(), order.getStatus());
                    return orderMapper.toDto(order);
                })
                .orElseThrow(() ->
                        new OrderException(ErrorType.NO_SUCH_ORDER_BY_UUID, orderId));
    }

    @Override
    @Transactional
    public void deleteOrder(UUID orderId) {
        log.info(LogType.ORDER_DELETE_START.getMessage(), orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new OrderException(ErrorType.NO_SUCH_ORDER_BY_UUID, orderId);
        }

        orderRepository.deleteById(orderId);
        log.info(LogType.ORDER_DELETED.getMessage(), orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOrderOwner(UUID orderId, CustomUserDetails userDetails) {
        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getId().equals(userDetails.user().getId()))
                .orElse(false);
    }
}