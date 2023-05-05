package se.evol.spring6withwebflux.controllers;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static se.evol.spring6withwebflux.services.TaxValidation.TAX_PATH;

@SpringBootTest
@AutoConfigureWebTestClient
@WireMockTest
class EmployerControlControllerTest {
    @Autowired
    WebTestClient webTestClient;
    private String passingId = "123";
    private String failingId = "456";
    private int port = 8443;

    WireMockServer wireMockServer = new WireMockServer(port);

    @BeforeEach
    public void setUp() {
        wireMockServer.start();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("All validations passes for org-id")
    void testAllValidationPasses () {
        wireMockServer.stubFor(get(urlEqualTo(TAX_PATH + passingId)).willReturn(ok().withBody("true")));
        webTestClient.get().uri(EmployerControlController.VALIDATION_PATH + passingId).exchange().expectBody(String.class).isEqualTo("{\"isValidOnTax\":true,\"isValidOnRegistration\":true}");
    }
    @Test
    @DisplayName("One validations fails in client call")
    void testValidationFailsOnOneClientCall () {
        wireMockServer.stubFor(get(urlEqualTo(TAX_PATH + failingId)).willReturn(ok().withBody("false")));
        webTestClient.get().uri(EmployerControlController.VALIDATION_PATH + failingId).exchange().expectBody(String.class).isEqualTo("{\"isValidOnTax\":false,\"isValidOnRegistration\":true}");
    }
}