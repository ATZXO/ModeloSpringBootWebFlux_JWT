package com.atz.webflux.controller;

import com.atz.webflux.dto.DishDTO;
import com.atz.webflux.model.Dish;
import com.atz.webflux.pagination.PageSupport;
import com.atz.webflux.service.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/dishes")
public class DishController {

    private final DishService dishService;
    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;

    @GetMapping
    private Mono<ResponseEntity<Flux<DishDTO>>> findAllDish() {
        Flux<DishDTO> dishes = dishService.findAll().map(this::convertToDto);
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(dishes)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DishDTO>> findById(@PathVariable String id) {
        return dishService.findById(id)
                .map(this::convertToDto)
                .map(dish -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(dish)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<DishDTO>> saveDish(@Valid @RequestBody DishDTO dishDTO, final ServerHttpRequest request) {
        return dishService.save(convertToDocument(dishDTO)).
                map(this::convertToDto).
                map(savedDish -> ResponseEntity.created(URI.create(request.getURI() + "/" + savedDish.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(savedDish));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<DishDTO>> updateDish(@Valid @PathVariable String id, @RequestBody DishDTO dishDTO) {
        return Mono.just(dishDTO)
                .flatMap(d -> {
                    d.setId(id);
                    return dishService.update(id, convertToDocument(d));
                })
                .map(this::convertToDto)
                .map(updatedDish -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(updatedDish)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Boolean>> deleteById(@PathVariable String id) {
        return dishService.deleteById(id)
                .flatMap(result -> {
                    if (result) {
                        return Mono.just(ResponseEntity.noContent().build());
                    } else {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                });
    }

    @GetMapping("/pegeable")
    public Mono<ResponseEntity<PageSupport<DishDTO>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return dishService.getPage(Dish.class, PageRequest.of(page, size))
                .map(pageSupport -> new PageSupport<>(
                        pageSupport.getContent().stream().map(this::convertToDto).toList(),
                        pageSupport.getPageNumber(), pageSupport.getPageSize(), pageSupport.getTotalElements()
                )).map(pageDTO -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(pageDTO))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //Ejemplo del uso de HATEOAS
    /*
    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<Dish>> getHateoas(@PathVariable String id) {
        Mono<Link> monoLink = linkTo(methodOn(DishController.class).findById(id)).withRel("dish-link").toMono();

        //Practica recomendada (sin usar .block(), sin variable global, sin map/flatmap anidados)

        return dishService.findById(id)
                .zipWith(monoLink, EntityModel::of); //(dish, link) -> EntityModel.of(dish, link)
    }
    */

    private DishDTO convertToDto(Dish dish) {
        return modelMapper.map(dish, DishDTO.class);
    }

    private Dish convertToDocument(DishDTO dishDTO) {
        return modelMapper.map(dishDTO, Dish.class);
    }
}








