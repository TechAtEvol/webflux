package se.evol.spring6withwebflux.exceptions;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class TaxValidationException extends RuntimeException{
    private String message;
    public TaxValidationException(String arg) {
        message = arg;
    }
}
