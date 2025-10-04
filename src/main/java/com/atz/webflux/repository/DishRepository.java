package com.atz.webflux.repository;

import com.atz.webflux.model.Dish;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends GenericRepository<Dish, String> {
}
