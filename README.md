# Store Management Project
Minimal Java Spring Boot & Hibernate &amp; PostgreSQL Store Management Backend

## Running steps for project
1. Clone the repository
2. Make sure you have Docker installed and running on your machine.
3. Navigate to the directory store-management, where `compose.yaml file is located and run the following command to start the PostgreSQL database:
```shell
docker-compose up -d
```
4. Make sure you have Java 21 installed on your machine.
5. In the same directory, where pom.xml file is located, run:
```shell
./mvnw clean install
./mvnw spring-boot:run
```
6. If you want to run tests
```shell
./mvnw test
```
