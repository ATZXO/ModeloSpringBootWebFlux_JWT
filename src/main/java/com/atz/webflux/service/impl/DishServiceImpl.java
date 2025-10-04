package com.atz.webflux.service.impl;

import com.atz.webflux.model.Dish;
import com.atz.webflux.repository.DishRepository;
import com.atz.webflux.repository.GenericRepository;
import com.atz.webflux.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends CRUDServiceImpl<Dish, String> implements DishService {

    private final DishRepository dishRepository;

    @Override
    protected GenericRepository<Dish, String> getRepo() {
        return dishRepository;
    }
}









