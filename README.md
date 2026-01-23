# Orders CRUD Microservice

## 1. Цель проекта

Разработать простой микросервис на **Java Spring Boot** для управления пользователями и заказами  
с использованием **JWT-аутентификации**, **ролевой модели доступа** и **CRUD-операций**.

Проект предназначен для демонстрации:
- архитектуры REST API
- Spring Security + JWT
- работы с PostgreSQL
- миграций БД
- Docker/Docker Compose
- unit и integration тестирования

---

## 2. Функциональные требования

### 2.1. Структура базы данных

#### Таблица `users`
| Поле | Тип | Описание |
|----|----|----|
| id | UUID | Уникальный идентификатор |
| username | String | Логин (уникальный) |
| password | String | Зашифрованный пароль |
| role | Enum | `USER`, `ADMIN` |

#### Таблица `orders`
| Поле | Тип | Описание |
|----|----|----|
| id | UUID | Уникальный идентификатор |
| user_id | UUID | Владелец заказа (FK → users.id) |
| description | String | Описание заказа |
| status | Enum | `CREATED`, `IN_PROGRESS`, `COMPLETED` |
| created_at | LocalDateTime | Дата создания |

---

## 3. API эндпоинты

### 3.1. Аутентификация (Spring Security + JWT)

| Метод | URL | Описание |
|----|----|----|
| POST | `/api/auth/register` | Регистрация пользователя (роль `USER` по умолчанию) |
| POST | `/api/auth/login` | Аутентификация, возвращает JWT |
| GET | `/api/auth/me` | Информация о текущем пользователе |

---

### 3.2. Пользователи (только `ADMIN`)

| Метод | URL | Описание |
|----|----|----|
| GET | `/api/users` | Получить список всех пользователей |
| DELETE | `/api/users/{id}` | Удалить пользователя |

---

### 3.3. Заказы

| Метод | URL | Доступ | Описание |
|----|----|----|----|
| POST | `/api/orders` | USER / ADMIN | Создать заказ |
| GET | `/api/orders` | USER / ADMIN | Заказы текущего пользователя |
| GET | `/api/orders/all` | ADMIN | Все заказы |
| PUT | `/api/orders/{id}` | ADMIN | Обновить статус заказа |
| DELETE | `/api/orders/{id}` | OWNER / ADMIN | Удалить заказ |

---

## 4. Технологический стек

- **Java** 17+
- **Spring Boot** 3+
- **Spring Security**
- **JWT**
- **Spring Data JPA**
- **PostgreSQL**
- **Flyway**
- **Swagger / OpenAPI**
- **Maven**
- **Docker / Docker Compose**
- **JUnit 5 / Mockito**

---

## 5. Запуск PostgreSQL

### 5.1 Для локальной разработки

```bash
docker run -d \
  --name crud-postgres \
  -p 5433:5432 \
  -e POSTGRES_DB=orders_db \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  postgres:15
```

### 5.2 Для интеграционных тестов
```bash
docker run -d \
  --name crud-postgres_IT \
  -p 5434:5432 \
  -e POSTGRES_DB=test_orders_db \
  -e POSTGRES_USER=test \
  -e POSTGRES_PASSWORD=test \
  postgres:15
```