package se.evol.spring6withwebflux.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.evol.spring6withwebflux.models.ValidationResult;
import se.evol.spring6withwebflux.services.RegistryValidation;
import se.evol.spring6withwebflux.services.TaxValidation;

@RequiredArgsConstructor
@Component
public class SequentialValidation {
    private final RegistryValidation registryValidation;
    private final TaxValidation taxValidation;

    public ValidationResult validate(String orgNo) {
        try {
            Boolean taxIsValid = taxValidation.isValid(orgNo).toFuture().get();
            Boolean registryIsValid = registryValidation.isValid(orgNo).toFuture().get();
            return ValidationResult.builder().isValidOnTax(taxIsValid).IsValidOnRegistration(registryIsValid).build();
        } catch (Exception err) {
            System.out.println("Not worth the time to do seq component properly " + err.getMessage());
            return ValidationResult.builder().isValidOnTax(false).IsValidOnRegistration(false).build();
        }
    }
}
