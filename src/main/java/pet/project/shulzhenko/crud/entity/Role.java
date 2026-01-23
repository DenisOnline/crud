package pet.project.shulzhenko.crud.entity;

import org.springframework.security.core.GrantedAuthority;

/**
 * Перечисление ролей пользователей.
 *
 * <p>Реализует интерфейс {@link GrantedAuthority} для использования в Spring Security.</p>
 *
 * <ul>
 *     <li>USER — обычный пользователь</li>
 *     <li>ADMIN — администратор</li>
 * </ul>
 */
public enum Role implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
