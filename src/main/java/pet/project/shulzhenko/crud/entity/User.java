package pet.project.shulzhenko.crud.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Сущность пользователя.
 *
 * <p>Отображается в таблице "users". Хранит учетные данные пользователя, его роль
 * и связанные заказы.</p>
 *
 * <p>Связи:</p>
 * <ul>
 *     <li>{@link Order} — список заказов пользователя (OneToMany, каскадное удаление)</li>
 * </ul>
 *
 * <p>Основные поля:</p>
 * <ul>
 *     <li>id — уникальный идентификатор пользователя</li>
 *     <li>username — уникальное имя пользователя</li>
 *     <li>password — хэш пароля пользователя</li>
 *     <li>role — роль пользователя (USER, ADMIN)</li>
 *     <li>orders — список заказов пользователя</li>
 * </ul>
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
}