package com.atz.webflux.service;

import com.atz.webflux.model.Dish;
import com.atz.webflux.repository.DishRepository;
import com.atz.webflux.service.impl.DishServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class DishServiceTest {
    @MockitoBean
    private DishService dishService;
    @MockitoBean
    private DishRepository dishRepository;

    @BeforeEach
    public void init() {
        dishService = new DishServiceImpl(dishRepository);
    }

    @Test
    public void findAllTest() {
        Mockito.when(dishService.findAll()).thenReturn(Flux.just(new Dish(), new Dish(), new Dish()));
        Flux<Dish> dishes = dishService.findAll();

        assertNotNull(dishes);

    }


















}
