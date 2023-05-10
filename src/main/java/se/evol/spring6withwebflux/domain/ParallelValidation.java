package se.evol.spring6withwebflux.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import se.evol.spring6withwebflux.models.ValidationResult;
import se.evol.spring6withwebflux.services.RegistryValidation;
import se.evol.spring6withwebflux.services.TaxValidation;

@RequiredArgsConstructor
@Component
public class ParallelValidation {
    private final RegistryValidation registryValidation;
    private final TaxValidation taxValidation;
    public Mono<ValidationResult> validate(String orgNo) {
        return Mono.zip(
                taxValidation.isValid(orgNo),
                registryValidation.isValid(orgNo),
                ValidationResult::new
        );
    }
}
