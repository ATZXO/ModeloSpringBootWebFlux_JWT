package com.atz.webflux.repository;

import com.atz.webflux.model.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends GenericRepository<User, String> {
    Mono<User> findOneByUsername(String username);
}
