package se.evol.spring6withwebflux.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RegistryValidation implements Validation{
    @Override
    public Mono<Boolean> isValid() {
        return Mono.just(true);
    }
}
