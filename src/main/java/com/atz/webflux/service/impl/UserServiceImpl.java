package com.atz.webflux.service.impl;

import com.atz.webflux.model.Role;
import com.atz.webflux.model.User;
import com.atz.webflux.repository.GenericRepository;
import com.atz.webflux.repository.RoleRepository;
import com.atz.webflux.repository.UserRepository;
import com.atz.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CRUDServiceImpl<User, String> implements UserService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;

    @Override
    protected GenericRepository<User, String> getRepo() {
        return userRepository;
    }

    @Override
    public Mono<User> saveHash(User user) {
        user.setPassword(bcrypt.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Mono<com.atz.webflux.security.User> searchByUser(String username) {
        return userRepository.findOneByUsername(username)
                .zipWhen(user -> Flux.fromIterable(user.getRoles())
                        .flatMap(role -> roleRepository.findById(role.getId()))
                                .map(Role::getName)
                                .collectList()
                        )
                .map(tuple ->
                        new com.atz.webflux.security.User(
                                tuple.getT1().getUsername(),
                                tuple.getT1().getPassword(),
                                tuple.getT1().isStatus(),
                                tuple.getT2()
                        ));
    }
}











