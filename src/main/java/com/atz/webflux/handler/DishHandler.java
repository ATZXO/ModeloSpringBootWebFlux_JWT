package com.atz.webflux.handler;

import com.atz.webflux.dto.DishDTO;
import com.atz.webflux.model.Dish;
import com.atz.webflux.service.DishService;
import com.atz.webflux.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class DishHandler {

    private final DishService dishService;
    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dishService.findAll().map(this::convertToDto), DishDTO.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");
        return dishService.findById(id)
                .map(this::convertToDto)
                .flatMap(dish -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(dish)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<DishDTO> dishDTOMono = request.bodyToMono(DishDTO.class);
        return dishDTOMono
                .flatMap(requestValidator::validate)
                .flatMap(dishDTO -> dishService.save(convertToDocument(dishDTO)))
                .map(this::convertToDto)
                .flatMap(savedDish -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(savedDish.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(savedDish)));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<DishDTO> dishDTOMono = request.bodyToMono(DishDTO.class);

        return dishDTOMono
                .map(dishDTO -> {
                    dishDTO.setId(id);
                    return dishDTO;
                })
                .flatMap(requestValidator::validate)
                .flatMap(distDTO -> dishService.update(id, convertToDocument(distDTO)))
                .map(this::convertToDto)
                .flatMap(dish -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(dish)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return dishService.deleteById(id)
                .flatMap(result -> {
                    if (result) {
                        return ServerResponse.noContent().build();
                    }else {
                        return ServerResponse.notFound().build();
                    }
                });
    }

    private DishDTO convertToDto(Dish dish) {
        return modelMapper.map(dish, DishDTO.class);
    }

    private Dish convertToDocument(DishDTO dishDTO) {
        return modelMapper.map(dishDTO, Dish.class);
    }
}
















