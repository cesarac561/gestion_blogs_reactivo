package com.bootcamp.reactive.blog.repositories;

import com.bootcamp.reactive.blog.entities.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByLoginAndPassword(String login, String password);

    Mono<Boolean> existsByLogin(String login);

    Mono<Boolean> existsByAuthorId(String authorId);

    Mono<User> findByAuthorId(String authorId);
}
