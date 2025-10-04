package com.atz.webflux.service.impl;

import com.atz.webflux.pagination.PageSupport;
import com.atz.webflux.repository.GenericRepository;
import com.atz.webflux.service.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class CRUDServiceImpl<T, ID> implements CRUDService<T, ID> {

    protected abstract GenericRepository<T, ID> getRepo();

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<T> save(T entity) {
        return getRepo().save(entity);
    }

    @Override
    public Mono<T> update(ID id, T entity) {
        return getRepo().findById(id)
                .flatMap(e -> getRepo().save(entity));
    }

    @Override
    public Flux<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public Mono<T> findById(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public Mono<Boolean> deleteById(ID id) {
        return getRepo().findById(id)
                .hasElement()  //Retorna un Mono<Boolean>
                .flatMap(result -> {
                    if (result) {
                        return getRepo().deleteById(id)
                                .thenReturn(true); //Retorna un Mono<Boolean> con valor true
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    @Override
    public Mono<PageSupport<T>> getPage(Class<T> entityClass, Pageable pageable) {
        //Procesamiento en db
        Query query = new Query()
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize());

        Mono<List<T>> list = mongoTemplate.find(query, entityClass).collectList();
        Mono<Long> count = mongoTemplate.count(new Query(), entityClass);

        return Mono.zip(list, count)
                .map(tuple -> new PageSupport<>(
                        tuple.getT1(),
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        tuple.getT2()
                ));
    }
}


















