package pet.project.shulzhenko.crud.utils;


import lombok.Getter;

/**
 * Перечисление шаблонов лог-сообщений приложения.
 */
@Getter
public enum LogType {

    //UserService
    USER_CREATE_START("Creating user. username={}"),
    USER_CREATED("User successfully created. userId={}, username={}"),
    USER_FETCH_ALL_START("Fetching all users..."),
    USER_FETCH_ALL_RESULT("Fetched all users. totalElements={}"),
    USER_DELETE_START("Delete user. userId={}"),
    USER_DELETED("User successfully deleted. userId={}"),

    //OrderService
    ORDER_CREATE_START("Creating order. userId={}"),
    ORDER_CREATED("Order successfully created. orderId={}, userId={}"),
    ORDER_FETCH_BY_USER_START("Fetching orders for user. userId={}, page={}, size={}"),
    ORDER_FETCH_BY_USER_RESULT("Fetched orders for user. userId={}, totalOrders={}"),
    ORDER_FETCH_ALL_START("Fetching all orders. page={}, size={}"),
    ORDER_FETCH_ALL_RESULT("Fetched all orders. totalOrders={}"),
    ORDER_STATUS_UPDATE_START("Updating order status. orderId={}, newStatus={}"),
    ORDER_STATUS_UPDATED("Deleting order. orderId={}"),
    ORDER_DELETE_START("Order successfully deleted. orderId={}"),
    ORDER_DELETED("Order status updated. orderId={}, status={}"),

    //AuthenticationService
    AUTH_LOGIN_ATTEMPT("Login attempt. username={}"),
    AUTH_LOGIN_SUCCESS("Login successful. userId={}, username={}"),
    AUTH_REGISTER_ATTEMPT("Registration attempt. username={}"),
    AUTH_PROFILE_FETCH("Fetching current user profile. username={}"),
    AUTH_TOKEN_REFRESH("Refreshing JWT token"),

    //GlobalExceptionHandler
    SERVICE_EXCEPTION("ServiceException [{}] [{}]: {}"),
    UNEXPECTED_EXCEPTION("Unexpected exception: {}"),
    VALIDATION_FAILED("Validation failed: {}"),
    INVALID_REQUEST_BODY("Invalid request body: {}");

    /**
     * Шаблон лог-сообщения.
     */
    private final String message;

    LogType(String message) {
        this.message = message;
    }

}