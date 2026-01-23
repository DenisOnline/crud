package pet.project.shulzhenko.crud.exeption;

import pet.project.shulzhenko.crud.entity.User;
import pet.project.shulzhenko.crud.utils.ErrorType;

/**
 * Исключение, связанное с операциями над пользователями.
 *
 * <p>Наследует {@link ServiceException} и используется для обработки ошибок
 * при работе с сущностью {@link User}.</p>
 */
public class UserException extends ServiceException {
    public UserException(ErrorType errorType) {
        super(errorType);
    }

    public UserException(ErrorType errorType, Object... args) {
        super(errorType, args);
    }
}