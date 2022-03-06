package com.bootcamp.reactive.blog.services;

import com.bootcamp.reactive.blog.entities.Author;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthorService {
    Mono<Author> findById(String id);
    Mono<Boolean> existsByEmail(String email);
    Flux<Author> findByEmail(String email);
    Flux<Author> findByName(String name);
    Flux<Author> findAll();
    Mono<Author> save(Author author);
    Mono<Author> saveWithValidation(Author author);
    Flux<Void> delete(String id);
    Mono<Author> update(Author author);
    
}
