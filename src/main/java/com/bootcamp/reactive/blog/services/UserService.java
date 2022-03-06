package com.bootcamp.reactive.blog.services;

import com.bootcamp.reactive.blog.entities.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Flux<User> findAll();
    Mono<User> findById(String id);
    Mono<User> findByAuthorId(String authorId);
    Mono<Boolean> existsByLogin(String login);
    Mono<Boolean> existsByAuthorId(String authorId);
    Mono<User> autenticarUsuario(String login, String password);
    Mono<User> save(User user);
    Mono<User> saveWithValidation(User user);
    Mono<User> update(User user);
    Mono<Void> delete(String id);
    Mono<Void> borrarUserbyAuthorId(String authorId);
}
