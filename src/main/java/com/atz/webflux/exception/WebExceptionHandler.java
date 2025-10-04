package com.atz.webflux.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) //Importancia alta. Tambien con @Order(-1) funciona
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public WebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        Throwable exception = getError(request);

        //Manejo de errores de validacion en API REST anotacionales (@Valid)
        if (exception instanceof WebExchangeBindException bindException) {
            Map<String, String> errors = bindException.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            DefaultMessageSourceResolvable::getDefaultMessage,
                            (msg1, msg2) -> msg1 + "; " + msg2
                    ));

            Map<String, Object> body = Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "errors", errors
            );

            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        }

        //Manejo de errores de validacion en API REST funcionales (RequestValidator)
        if (exception instanceof ConstraintViolationException cve) {
            Map<String, String> errors = cve.getConstraintViolations().stream()
                    .collect(Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage,
                            (msg1, msg2) -> msg1 + "; " + msg2 //Por si hay mas de un error en el mismo campo
                    ));

            Map<String, Object> body = Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "errors", errors
            );

            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        }

        //Manejo de otros errores genericos
        Map<String, Object> defaultError = getErrorAttributes(request, ErrorAttributeOptions.defaults());

        int statusCode = Integer.valueOf(String.valueOf(defaultError.get("status")));

        CustomErrorResponse errorResponse;

        switch (statusCode) {
            case 404 -> errorResponse = new CustomErrorResponse(LocalDateTime.now(), "Resource not found");
            case 401, 403 -> errorResponse = new CustomErrorResponse(LocalDateTime.now(), "Not authorized");
            case 500 -> errorResponse = new CustomErrorResponse(LocalDateTime.now(), "Internal server error");
            default -> errorResponse = new CustomErrorResponse(LocalDateTime.now(), exception.getMessage());
        }
        return ServerResponse.status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }
}















