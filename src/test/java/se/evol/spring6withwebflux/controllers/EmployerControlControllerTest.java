package se.evol.spring6withwebflux.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.*;
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
    void testAllValidationPasses () throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String taxBody = mapper.writeValueAsString(TaxValidationResponse.builder().fSkatt("true").build());
        wireMockServer.stubFor(get(urlEqualTo(TAX_PATH + passingId)).willReturn(ok()
                .withHeader("Content-Type", "application/json")
                .withBody(taxBody))
        );
        String registryBody = mapper.writeValueAsString(RegistryValidationResponse.builder().isValid("true").build());
        wireMockServer.stubFor(get(urlEqualTo(REGISTRY_PATH + passingId))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(registryBody))
        );
        webTestClient.get().uri(EmployerControlController.VALIDATION_PATH + passingId).exchange().expectBody(String.class).isEqualTo("{\"isValidOnTax\":true,\"isValidOnRegistration\":true}");
    }
    @Test
    @DisplayName("One validations fails in client call")
    void testValidationFailsOnOneClientCall () throws JsonProcessingException {
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
    @DisplayName("Validation exits early on fails")
    void testValidationFailsWithEarlyExit () throws JsonProcessingException {
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
    @Test
    // @Disabled
    @DisplayName("Slow when both returns true")
    void testValidationThatIsSlowToProveAPoint () throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String taxBody = mapper.writeValueAsString(TaxValidationResponse.builder().fSkatt("true").build());
        wireMockServer.stubFor(get(urlEqualTo(TAX_PATH + failingId))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(taxBody))
        );
        String registryBody = mapper.writeValueAsString(RegistryValidationResponse.builder().isValid("true").build());
        wireMockServer.stubFor(get(urlEqualTo(REGISTRY_PATH + failingId))
                .willReturn(ok()
                        .withFixedDelay(3000)
                        .withHeader("Content-Type", "application/json")
                        .withBody(registryBody))
        );
        webTestClient.get().uri(EmployerControlController.VALIDATION_PATH_EARLY_EXIT + failingId).exchange().expectBody(String.class).isEqualTo("{\"isValidOnTax\":true,\"isValidOnRegistration\":true}");
    }
}