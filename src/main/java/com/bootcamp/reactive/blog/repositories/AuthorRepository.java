package com.bootcamp.reactive.blog.repositories;

import com.bootcamp.reactive.blog.entities.Author;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AuthorRepository extends ReactiveMongoRepository<Author,String> {

    Flux<Author> findByEmail(String email);

    Flux<Author> findByName(String name);

    Mono<Boolean>existsByEmail(String email);
}
