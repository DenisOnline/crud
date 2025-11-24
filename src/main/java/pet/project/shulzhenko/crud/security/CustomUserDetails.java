package pet.project.shulzhenko.crud.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pet.project.shulzhenko.crud.entity.User;

import java.util.Collection;
import java.util.Collections;

/**
 * Детали пользователя для Spring Security.
 *
 * <p>Реализует интерфейс {@link UserDetails} для использования в контексте безопасности.
 * Оборачивает сущность {@link User} и предоставляет информацию о правах доступа и учетных данных.</p>
 *
 * @param user объект пользователя
 */
public record CustomUserDetails(User user) implements UserDetails {

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(user.getRole());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}