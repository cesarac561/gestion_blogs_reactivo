package com.bootcamp.reactive.blog.repositories;

import com.bootcamp.reactive.blog.entities.Blog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface BlogRepository extends ReactiveMongoRepository<Blog, String> {

    Mono<Long> countByAuthorIdAndStatus(String authorId, String status);
}