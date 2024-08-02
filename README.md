# Store Management API

## Project Description
This is a minimal Java Spring Boot & PostgreSQL backend API for managing a store's products.

## Technologies Used
- Java 21
- Spring Boot
- Maven
- PostgreSQL (via Docker)
- SLF4J and Logback for logging
- JUnit and Mockito for testing
- Spring Boot Actuator for health checks and monitoring
- SpringDoc OpenAPI for API documentation

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
2. Navigate to the project directory:
    ```bash
    cd store-management
    ```
3. Start the PostgreSQL database using Docker:
    ```bash
    docker-compose up -d
    ```
4. Build and run the application:
    ```bash
    ./mvnw clean install
    ./mvnw spring-boot:run
    ```

### Default Admin User
An admin user is created at startup with the following credentials:
- Username: `admin`
- Password: `admin`

## API Endpoints Examples
- **Add Product**: `POST /api/products`
- **Find Product**: `GET /api/products/{id}`
- **Update Product Price**: `PUT /api/products/{id}/price`
- **Increase Product Quantity**: `PATCH /api/products/{id}/increaseQuantity`

## Authentication and Authorization
- Basic authentication is implemented.
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

## Postman Collection
A Postman collection for testing the API endpoints is available in the root directory: `postman/Store Management API.postman_collection.json`

## Testing
- Unit tests are written using JUnit and Mockito.
- To run tests:
    ```bash
    ./mvnw test
    ```

## DTOs
- The API uses Data Transfer Objects (DTOs) to decouple the internal data structures from the API responses and requests.
- This enhances security and allows more control over the data exposed to the clients.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
