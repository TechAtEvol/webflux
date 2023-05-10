package se.evol.spring6withwebflux.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import se.evol.spring6withwebflux.exceptions.RegistryValidationException;
import se.evol.spring6withwebflux.exceptions.TaxValidationException;
import se.evol.spring6withwebflux.models.ValidationResult;
import se.evol.spring6withwebflux.services.RegistryValidation;
import se.evol.spring6withwebflux.services.TaxValidation;

@RequiredArgsConstructor
@Component
public class ParallelValidationWithEarlyExit {
    private final RegistryValidation registryValidation;
    private final TaxValidation taxValidation;
    public Mono<ValidationResult> validate(String orgNo) {
        return Mono.zip(
                taxValidation.isValid(orgNo).doOnNext( val -> {
                    if (!val) throw new TaxValidationException("Tax invalid");
                }),
                registryValidation.isValid(orgNo).doOnNext( val -> {
                    if (!val) throw new RegistryValidationException("Registry invalid");
                }),
                ValidationResult::new
        ).onErrorResume(err -> {
            if(err instanceof RegistryValidationException) {
                ValidationResult failedRegistryCheck = ValidationResult.builder().isValidOnTax(true).IsValidOnRegistration(false).build();
                return Mono.just(failedRegistryCheck);
            }
            if(err instanceof TaxValidationException) {
                ValidationResult failedRegistryCheck = ValidationResult.builder().isValidOnTax(false).IsValidOnRegistration(true).build();
                return Mono.just(failedRegistryCheck);
            }
            // Do something clever for regular errors
            ValidationResult failed =  ValidationResult.builder().isValidOnTax(false).IsValidOnRegistration(false).build();
            return Mono.just(failed);
        });
    }
}
