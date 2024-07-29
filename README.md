# Store Management API

## Project Description
This is a minimal Java Spring Boot &amp; PostgreSQL backend API for managing a store's products.

## Technologies Used
- Java 21
- Spring Boot
- Maven
- PostgreSQL (via Docker)
- SLF4J and Logback for logging
- JUnit and Mockito for testing

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
- Logs are written to console and rolling files.

## Testing
- Unit tests are written using JUnit and Mockito.
- To run tests:
    ```bash
    ./mvnw test
    ```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
