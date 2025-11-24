package pet.project.shulzhenko.crud.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность заказа.
 *
 * <p>Отображается в таблице "orders". Содержит информацию о заказе пользователя,
 * его статус, описание и время создания.</p>
 *
 * <p>Связи:</p>
 * <ul>
 *     <li>{@link User} — владелец заказа (ManyToOne)</li>
 * </ul>
 *
 * <p>Особенности:</p>
 * <ul>
 *     <li>Статус заказа по умолчанию устанавливается в {@link Status#CREATED} при создании</li>
 *     <li>Время создания задается автоматически через {@link PrePersist}</li>
 * </ul>
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @Setter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String description;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        status = Status.CREATED;
        createdAt = LocalDateTime.now();
    }
}