# Java Backend Starter API

## Project Description
This is a minimal Java Spring backend API for managing a store's products.

## Technologies Used
- Java 21
- Spring Boot
- Maven
- PostgreSQL (via Docker)
- SLF4J and Logback for logging
- JUnit and Mockito for testing
- Spring Boot Actuator for health checks and monitoring
- SpringDoc OpenAPI for API documentation
- Pagination and sorting for products
- Pagination, sorting & filters for users
- Spring Cache for products and users
- Indexing
- JWT for authentication

## Setup and Installation

### Prerequisites
- Java 21
- Maven
- Docker (for PostgreSQL)

### Steps to Run the Application
1. Clone the repository:
    ```bash
    git clone https://github.com/grecuandrei/store-management.git
    ```
2. Navigate to the project directory where Dockerfile:
    ```bash
    cd store-management
    ```
3. Build and run the Docker containers:
    ```bash
    docker-compose up --build
    ```

### Default Admin User
An admin user is created at startup with the following credentials:
- Email: `admin@admin.com`
- Password: `admin`

## API Endpoints Examples
- **Add Product**: `POST /api/products`
- **Find Product**: `GET /api/products/{id}`
- **Update Product Price**: `PUT /api/products/{id}/price`
- **Increase Product Quantity**: `PATCH /api/products/{id}/increaseQuantity`

## DTOs
- The API uses Data Transfer Objects (DTOs) to decouple the internal data structures from the API responses and requests.
- This enhances security and allows more control over the data exposed to the clients.

## Caching & Indexing
- Spring Cache for caching products and users to improve performance for queries.
- The cache configurations can be found in the CacheConfig class.

## Pagination & Sorting
- Pagination and sorting are implemented for fetching products and users.
- The Pageable interface from Spring Data JPA is used for this purpose.

## Filtering
- Custom filtration is implemented with the help of Specification class 
- Custom Response and Query Classes

## Authentication and Authorization
- JWT authentication is implemented.
- Role-based access control with roles such as USER and ADMIN.

## Error Handling
- Custom exception handling with meaningful HTTP status codes and messages.

## Logging
- Configured using SLF4J and Logback.
- Logs are written to console and rolling files in the root directory: `logs/`.

## Actuator Endpoints
- **Health Check**: `GET /actuator/health`
- **Info**: `GET /actuator/info`

## API Documentation
- Swagger UI is available at: `http://localhost:8080/swagger-ui.html`

## Postman Collection - TODO Update
A Postman collection for testing the API endpoints is available in the root directory: `postman/Store Management API.postman_collection.json`

## Testing
- Unit tests are written using JUnit and Mockito.
- To run tests:
    ```bash
    ./mvnw test
    ```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
