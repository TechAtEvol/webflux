package se.evol.spring6withwebflux.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import se.evol.spring6withwebflux.domain.ParallelValidation;
import se.evol.spring6withwebflux.domain.ParallelValidationWithEarlyExit;
import se.evol.spring6withwebflux.domain.SequentialValidation;
import se.evol.spring6withwebflux.models.ValidationResult;

@RestController
@RequiredArgsConstructor
public class EmployerControlController {
    public static final String VALIDATION_PATH = "/api/validate/";
    public static final String VALIDATION_PATH_WITH_ID = VALIDATION_PATH + "{org-no}";
    private final ParallelValidation parallelValidation;
    public static final String VALIDATION_PATH_EARLY_EXIT = "/api/validate-with-exit/";
    public static final String VALIDATION_PATH_EARLY_EXIT_WITH_ID = VALIDATION_PATH_EARLY_EXIT + "{org-no}";
    private final ParallelValidationWithEarlyExit parallelValidationWithEarlyExit;

    public static final String VALIDATION_PATH_SEQ = "/api/validate-with-seq/";
    public static final String VALIDATION_PATH_SEQ_WITH_ID = VALIDATION_PATH_SEQ + "{org-no}";
    private final SequentialValidation sequentialValidation;

    @GetMapping(VALIDATION_PATH_WITH_ID)
    Mono<ValidationResult> getValidation(@PathVariable("org-no") String orgNo) {
        return parallelValidation.validate(orgNo);
    }

    @GetMapping(VALIDATION_PATH_EARLY_EXIT_WITH_ID)
    Mono<ValidationResult> getValidationWithEarlyExit(@PathVariable("org-no") String orgNo) {
        return parallelValidationWithEarlyExit.validate(orgNo);
    }

    @GetMapping(VALIDATION_PATH_SEQ_WITH_ID)
    ValidationResult getValidationInSequence(@PathVariable("org-no") String orgNo) {
        return sequentialValidation.validate(orgNo);
    }
}
