package se.evol.spring6withwebflux.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.evol.spring6withwebflux.models.ValidationResult;
import se.evol.spring6withwebflux.services.RegistryValidation;
import se.evol.spring6withwebflux.services.TaxValidation;

@RestController
@RequiredArgsConstructor
public class EmployerControlController {
    private static final String VALIDATION_PATH = "/api/validate";
    private final RegistryValidation registryValidation;
    private final TaxValidation taxValidation;

    @GetMapping(VALIDATION_PATH)
    Mono<ValidationResult> getValidation() {
        return Mono.zip(
                registryValidation.isValid(),
                taxValidation.isValid(),
                ValidationResult::new);
    }
}
