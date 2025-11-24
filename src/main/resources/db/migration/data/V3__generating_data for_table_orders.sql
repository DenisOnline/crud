-- Автоматическая генерация 0-5 заказов для каждого пользователя
DO $$
DECLARE
    u RECORD;
    num_orders INT;
    i INT;
BEGIN
    FOR u IN SELECT id, username FROM users LOOP
         -- случайное количество заказов от 0 до 5
        num_orders := floor(random() * 6)::int;
        FOR i IN 1..num_orders LOOP
                INSERT INTO orders (user_id, description, status, created_at)
                VALUES (u.id, concat('Order ', i, ' for ', u.username), 'CREATED', NOW());
        END LOOP;
    END LOOP;
END $$;