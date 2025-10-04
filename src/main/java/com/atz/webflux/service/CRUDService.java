package com.atz.webflux.service;

import com.atz.webflux.pagination.PageSupport;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CRUDService<T, ID> {
    Mono<T> save(T entity);
    Mono<T> update(ID id, T entity);
    Flux<T> findAll();
    Mono<T> findById(ID id);
    Mono<Boolean> deleteById(ID id);
    Mono<PageSupport<T>> getPage(Class<T> entityClass, Pageable pageable);
}











