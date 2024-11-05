package br.com.dkzit.project;

import br.com.dkzit.project.domain.entity.CustomerEntity;
import br.com.dkzit.project.repository.CustomerRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CustomerControllerTest {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:17"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.url", postgres::getJdbcUrl);
        registry.add("spring.data.username", postgres::getUsername);
        registry.add("spring.data.password", postgres::getPassword);
    }

    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void shouldGetAllCustomers() {
        List<CustomerEntity> customers = List.of(
                new CustomerEntity(null, "John", "Doe"),
                new CustomerEntity(null, "Alex", "Kid")
        );

        customerRepository.saveAll(customers);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/customers")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(".", hasSize(2));
    }

    void shouldCreateCustomer() {
        CustomerEntity customerEntity = new CustomerEntity(null, "John", "Doe");

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(customerEntity)
                .post("/api/customers")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("firstName", equalTo(customerEntity.getFirstName()))
                .body("lastName", equalTo(customerEntity.getLastName()));

    }

    @Test
    void shouldUpdateCustomer() {
        CustomerEntity customerEntity = new CustomerEntity(null, "John", "Doe");
        customerRepository.save(customerEntity);

        CustomerEntity updatedCustomer = new CustomerEntity(customerEntity.getId(), "John", "Updated");

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(updatedCustomer)
                .put("/api/customers/" + customerEntity.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("firstName", equalTo(updatedCustomer.getFirstName()))
                .body("lastName", equalTo(updatedCustomer.getLastName()));
    }

    @Test
    void shouldDeleteCustomer() {
        CustomerEntity customerEntity = new CustomerEntity(null, "John", "Doe");
        customerRepository.save(customerEntity);

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/customers/" + customerEntity.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }



}