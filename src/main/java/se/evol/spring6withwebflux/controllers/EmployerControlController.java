package se.evol.spring6withwebflux.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import se.evol.spring6withwebflux.domain.ParallelValidation;
import se.evol.spring6withwebflux.models.ValidationResult;

@RestController
@RequiredArgsConstructor
public class EmployerControlController {
    public static final String VALIDATION_PATH = "/api/validate/";
    public static final String VALIDATION_PATH_WITH_ID = VALIDATION_PATH + "{org-no}";
    private final ParallelValidation parallelValidation;

    @GetMapping(VALIDATION_PATH_WITH_ID)
    Mono<ValidationResult> getValidation(@PathVariable("org-no") String orgNo) {
        return parallelValidation.validate(orgNo);
    }
}
