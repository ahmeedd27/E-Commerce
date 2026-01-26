# E-Commerce API

This is a **Spring Boot** based RESTful API for an e-commerce platform. It supports features like user authentication, product management, cart operations, order processing, and product commenting, all secured with **JWT**-based authentication and role-based access control.

---

## Features

- **User Authentication**: Register, login, and email confirmation.
- **Password Management**: Change password for authenticated users.
- **Role-based Access**: Users can be assigned roles (`USER`, `ADMIN`).
- **Product Management**: Admins can create, update, delete, and retrieve products.
- **Cart Management**: Users can add, view, remove, and clear cart items.
- **Order Management**: Users and admins can view and manage orders.
- **Product Commenting**: Authenticated users can comment on products.
- **Pagination**: Support for paginated product listings.

---

## Technologies

- **Java**: 17+
- **Spring Boot**: 3.x
- **Spring Security**: For authentication and authorization
- **JWT**: Stateless authentication
- **Spring Data JPA**: Database operations
- **Hibernate Validator**: Input validation
- **Lombok**: Reduces boilerplate code
- **Database**: Configurable (e.g., MySQL, PostgreSQL, H2 for testing)
- ## **Maven**: Dependency management

## API Endpoints

### Authentication (`/api/auth`)

| Method | Endpoint           | Description                        | Roles         | Request Body/Example                                                   |
| ------ | ------------------ | ---------------------------------- | ------------- | ---------------------------------------------------------------------- |
| POST   | `/login`           | Authenticate user and return JWT   | Any           | `{"email": "user@example.com", "password": "pass123"}`                 |
| POST   | `/register`        | Register a new user                | Any           | `{"email": "user@example.com", "password": "pass123", "role": "USER"}` |
| POST   | `/change-password` | Change password for logged-in user | Authenticated | `{"oldPassword": "pass123", "newPassword": "newPass123"}`              |
| POST   | `/confirm-email`   | Confirm email with code            | Any           | `{"email": "user@example.com", "confirmationCode": "123456"}`          |
| GET    | `/user/role`       | Get role of authenticated user     | Authenticated | None                                                                   |
| GET    | `/user/{id}`       | Get user email by ID               | Any           | None                                                                   |

### Cart (`/api/cart`)

| Method | Endpoint       | Description              | Roles         | Parameters/Request Body  |
| ------ | -------------- | ------------------------ | ------------- | ------------------------ |
| POST   | `/add`         | Add product to cart      | Authenticated | `productId=1&quantity=2` |
| GET    | `/`            | Get user's cart          | Authenticated | None                     |
| DELETE | `/`            | Clear user's cart        | Authenticated | None                     |
| DELETE | `/{productId}` | Remove product from cart | Authenticated | None                     |

### Comments (`/api/comments`)

| Method | Endpoint               | Description                    | Roles         | Request Body/Example            |
| ------ | ---------------------- | ------------------------------ | ------------- | ------------------------------- |
| POST   | `/product/{productId}` | Add comment to a product       | Authenticated | `{"content": "Great product!"}` |
| GET    | `/product/{productId}` | Get all comments for a product | Any           | None                            |

### Orders (`/api/orders`)

| Method | Endpoint            | Description                       | Roles         | Parameters/Request Body                     |
| ------ | ------------------- | --------------------------------- | ------------- | ------------------------------------------- |
| POST   | `/`                 | Create a new order                | Authenticated | `address=123 Street&phoneNumber=1234567890` |
| GET    | `/`                 | Get all orders                    | ADMIN         | None                                        |
| GET    | `/user`             | Get orders for authenticated user | Authenticated | None                                        |
| PUT    | `/{orderId}/status` | Update order status               | ADMIN         | `status=SHIPPED`                            |

### Products (`/api/products`)

| Method | Endpoint | Description                  | Roles | Request Body/Example                                                    |
| ------ | -------- | ---------------------------- | ----- | ----------------------------------------------------------------------- |
| POST   | `/`      | Create a new product         | ADMIN | Multipart: `product={"name": "Item", "price": 10.0}` + image (optional) |
| PUT    | `/{id}`  | Update a product             | ADMIN | Multipart: `product={"name": "Item", "price": 12.0}` + image (optional) |
| DELETE | `/{id}`  | Delete a product             | ADMIN | None                                                                    |
| GET    | `/{id}`  | Get a product by ID          | Any   | None                                                                    |
| GET    | `/`      | Get all products (paginated) | Any   | Query params: `page=0&size=10`                                          |

---

## Security

- **JWT Authentication**: Most endpoints require a valid JWT token in the Authorization header (Bearer `<token>`).
- **Role-Based Access**:
  - **USER**: Can access cart, orders, and comment endpoints.
  - **ADMIN**: Can manage products and update order statuses.
- **CSRF**: Disabled for stateless REST API.
- **Input Validation**: Uses `@Valid` and Hibernate Validator for request bodies.
