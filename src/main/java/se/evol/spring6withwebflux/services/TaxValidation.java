package se.evol.spring6withwebflux.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TaxValidation implements Validation{
    public static final String TAX_PATH = "/api/foretag/";
    WebClient client = WebClient.builder().baseUrl("http://localhost:8443").defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    private String extracted;
    @Override
    public Mono<Boolean> isValid(String orgNo) {
        return client.get().uri(TAX_PATH + orgNo).retrieve().bodyToMono(String.class).map(Boolean::parseBoolean);
    }
}
