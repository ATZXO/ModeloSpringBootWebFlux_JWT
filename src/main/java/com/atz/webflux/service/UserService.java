package com.atz.webflux.service;

import com.atz.webflux.model.User;
import reactor.core.publisher.Mono;

public interface UserService extends CRUDService<User, String> {

    Mono<User> saveHash(User user);
    Mono<com.atz.webflux.security.User> searchByUser(String username);
}
