package com.atz.webflux.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RequestValidator { //Validacion para los endpoint funcionales, no se puede usar @Valid

    private final Validator validator;

    public <T> Mono<T> validate(T object) {
        if (object == null) {
            return Mono.error(new ConstraintViolationException("Invalid request", Set.of()));
        }

        Set<ConstraintViolation<T>> constraints = validator.validate(object);

        if(constraints == null || constraints.isEmpty()) {
            return Mono.just(object);
        } else {
            return Mono.error(new ConstraintViolationException(constraints));
        }

    }
}





