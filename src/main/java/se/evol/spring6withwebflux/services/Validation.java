package se.evol.spring6withwebflux.services;

import reactor.core.publisher.Mono;

public interface Validation {
    Mono<Boolean> isValid(String orgNo);
}
