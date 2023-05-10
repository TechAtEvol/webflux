package se.evol.spring6withwebflux.exceptions;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class RegistryValidationException extends RuntimeException{
    private String message;
    public RegistryValidationException(String arg) {
        message = arg;
    }
}
