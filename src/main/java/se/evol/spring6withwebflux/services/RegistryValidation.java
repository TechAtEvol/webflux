package se.evol.spring6withwebflux.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import se.evol.spring6withwebflux.models.RegistryValidationResponse;

@Service
@RequiredArgsConstructor
public class RegistryValidation implements Validation{
    public static final String REGISTRY_PATH = "/api/bolag/";
    WebClient client = WebClient.builder().baseUrl("http://localhost:8443").defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    private String extracted;

    @Override
    public Mono<Boolean> isValid(String orgNo) {
        return client.get().uri(REGISTRY_PATH + orgNo).retrieve().bodyToMono(RegistryValidationResponse.class)
                .map(RegistryValidationResponse::getIsValid)
                .map(Boolean::parseBoolean);
    }
}
