package pet.project.shulzhenko.crud.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pet.project.shulzhenko.crud.entity.Order;
import pet.project.shulzhenko.crud.entity.User;

import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link Order}.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Получает все заказы с поддержкой пагинации.
     *
     * @param pageable параметры пагинации
     * @return страница заказов
     */
    Page<Order> findAllBy(Pageable pageable);

    /**
     * Получает заказы конкретного пользователя с поддержкой пагинации.
     *
     * @param user пользователь
     * @param pageable параметры пагинации
     * @return страница заказов пользователя
     */
    Page<Order> findAllByUser(User user, Pageable pageable);

}