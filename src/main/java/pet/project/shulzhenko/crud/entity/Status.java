package pet.project.shulzhenko.crud.entity;

/**
 * Перечисление статусов заказа.
 *
 * <ul>
 *     <li>CREATED — заказ создан</li>
 *     <li>IN_PROGRESS — заказ в обработке</li>
 *     <li>COMPLETED — заказ выполнен</li>
 * </ul>
 */
public enum Status {
    CREATED,
    IN_PROGRESS,
    COMPLETED
}