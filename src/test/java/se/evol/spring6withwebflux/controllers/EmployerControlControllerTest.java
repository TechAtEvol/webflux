package se.evol.spring6withwebflux.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class EmployerControlControllerTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    @DisplayName("All validations passes for org-id")
    void testAllValidationPasses () {
        webTestClient.get().uri(EmployerControlController.VALIDATION_PATH).exchange().expectStatus().isOk();
    }
}