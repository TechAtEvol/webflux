package se.evol.spring6withwebflux.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import se.evol.spring6withwebflux.models.RegistryValidationResponse;
import se.evol.spring6withwebflux.models.TaxValidationResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static se.evol.spring6withwebflux.services.RegistryValidation.REGISTRY_PATH;
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

    private int delayForPositiveCalls = 250;

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
    @DisplayName("Sequential validations that passes, and takes twice the time")
    void testAllSeqValidationPasses() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String taxBody = mapper.writeValueAsString(TaxValidationResponse.builder().fSkatt("true").build());
        wireMockServer.stubFor(get(urlEqualTo(TAX_PATH + passingId)).willReturn(ok()
                .withFixedDelay(delayForPositiveCalls)
                .withHeader("Content-Type", "application/json")
                .withBody(taxBody))
        );
        String registryBody = mapper.writeValueAsString(RegistryValidationResponse.builder().isValid("true").build());
        wireMockServer.stubFor(get(urlEqualTo(REGISTRY_PATH + passingId))
                .willReturn(ok()
                        .withFixedDelay(delayForPositiveCalls)
                        .withHeader("Content-Type", "application/json")
                        .withBody(registryBody))
        );
        webTestClient.get().uri(EmployerControlController.VALIDATION_PATH_SEQ + passingId).exchange().expectBody(String.class).isEqualTo("{\"isValidOnTax\":true,\"isValidOnRegistration\":true}");
    }

    @Test
    @DisplayName("All parallel validations passes for org-id")
    void testAllValidationPasses() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String taxBody = mapper.writeValueAsString(TaxValidationResponse.builder().fSkatt("true").build());
        wireMockServer.stubFor(get(urlEqualTo(TAX_PATH + passingId)).willReturn(ok()
                .withFixedDelay(delayForPositiveCalls)
                .withHeader("Content-Type", "application/json")
                .withBody(taxBody))
        );
        String registryBody = mapper.writeValueAsString(RegistryValidationResponse.builder().isValid("true").build());
        wireMockServer.stubFor(get(urlEqualTo(REGISTRY_PATH + passingId))
                .willReturn(ok()
                        .withFixedDelay(delayForPositiveCalls)
                        .withHeader("Content-Type", "application/json")
                        .withBody(registryBody))
        );
        webTestClient.get().uri(EmployerControlController.VALIDATION_PATH + passingId).exchange().expectBody(String.class).isEqualTo("{\"isValidOnTax\":true,\"isValidOnRegistration\":true}");
    }

    @Test
    @DisplayName("One validation fail in parallel client calls, both calls are awaited")
    void testValidationFailsOnOneClientCall() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String taxBody = mapper.writeValueAsString(TaxValidationResponse.builder().fSkatt("false").build());
        wireMockServer.stubFor(get(urlEqualTo(TAX_PATH + failingId))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(taxBody))
        );
        String registryBody = mapper.writeValueAsString(RegistryValidationResponse.builder().isValid("true").build());
        wireMockServer.stubFor(get(urlEqualTo(REGISTRY_PATH + failingId))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(registryBody))
        );
        webTestClient.get().uri(EmployerControlController.VALIDATION_PATH + failingId).exchange().expectBody(String.class).isEqualTo("{\"isValidOnTax\":false,\"isValidOnRegistration\":true}");
    }

    @Test
    @DisplayName("Validation with early exit on fail")
    void testValidationFailsWithEarlyExit() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String taxBody = mapper.writeValueAsString(TaxValidationResponse.builder().fSkatt("false").build());
        wireMockServer.stubFor(get(urlEqualTo(TAX_PATH + failingId))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(taxBody))
        );
        String registryBody = mapper.writeValueAsString(RegistryValidationResponse.builder().isValid("true").build());
        wireMockServer.stubFor(get(urlEqualTo(REGISTRY_PATH + failingId))
                .willReturn(ok()
                        .withFixedDelay(5000)
                        .withHeader("Content-Type", "application/json")
                        .withBody(registryBody))
        );
        webTestClient.get().uri(EmployerControlController.VALIDATION_PATH_EARLY_EXIT + failingId).exchange().expectBody(String.class).isEqualTo("{\"isValidOnTax\":false,\"isValidOnRegistration\":true}");
    }
}