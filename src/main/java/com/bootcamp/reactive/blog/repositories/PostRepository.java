package com.bootcamp.reactive.blog.repositories;

import com.bootcamp.reactive.blog.entities.Post;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface PostRepository extends ReactiveMongoRepository<Post, String> {
    Mono<Boolean> existsByBlogIdAndStatusAndDate(String blogId, String status, Date date);

    Mono<Void> deleteByBlogId(String blogId);

    Flux<Post> findByBlogId(String blogId);
}
