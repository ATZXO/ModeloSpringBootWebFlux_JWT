package com.atz.webflux.controller;

import com.atz.webflux.dto.DishDTO;
import com.atz.webflux.model.Dish;
import com.atz.webflux.service.DishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(controllers = DishController.class)
public class DishControllerTest {

    @Autowired
    private WebTestClient client;
    @MockitoBean
    private DishService service;
    @MockitoBean
    @Qualifier("defaultMapper")
    private ModelMapper modelMapper;
    @MockitoBean
    private WebProperties.Resources resources;

    private Dish dish1;
    private Dish dish2;
    private DishDTO dishDTO1;
    private DishDTO dishDTO2;
    private List<Dish> dishes;

    @BeforeEach
    public void init(){
        dish1 = new Dish("1", "Pasta", 12.99, true);
        dish2 = new Dish("2", "Pizza",  15.99, true);
        dishDTO1 = new DishDTO("1", "Pasta", 12.99, true);
        dishDTO2 = new DishDTO("2", "Pizza", 15.99, true);
        dishes = List.of(dish1, dish2);
    }

    @Test
    public void findAllTest() {
        Mockito.when(service.findAll()).thenReturn(Flux.fromIterable(dishes));
        Mockito.when(modelMapper.map(dish1, DishDTO.class)).thenReturn(dishDTO1);
        Mockito.when(modelMapper.map(dish2, DishDTO.class)).thenReturn(dishDTO2);

        client.get()
                .uri("/v1/dishes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void saveTest() {
        Mockito.when(service.save(any())).thenReturn(Mono.just(dish1));
        Mockito.when(modelMapper.map(dish1, DishDTO.class)).thenReturn(dishDTO1);

        client.post()
                .uri("/v1/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dishDTO1)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.price").isNumber()
                .jsonPath("$.status").isBoolean();
    }

    @Test
    public void updateTest() {
        Mockito.when(service.update(any(), any())).thenReturn(Mono.just(dish2));
        Mockito.when(modelMapper.map(dish2, DishDTO.class)).thenReturn(dishDTO2);

        client.put()
                .uri("/v1/dishes/{id}", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dishDTO2)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.price").isNumber()
                .jsonPath("$.status").isBoolean();
    }

    @Test
    public void deleteTest() {
        Mockito.when(service.deleteById(any())).thenReturn(Mono.just(true));

        client.delete()
                .uri("/v1/dishes/{id}", "1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void deleteNotFoundTest() {
        Mockito.when(service.deleteById(any())).thenReturn(Mono.just(false));

        client.delete()
                .uri("/v1/dishes/{id}", "1")
                .exchange()
                .expectStatus().isNotFound();
    }



















}
