package com.bootcamp.reactive.blog.services;

import com.bootcamp.reactive.blog.entities.Blog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface BlogService {
    Mono<Blog> findById(String id);
    Flux<Blog> findAll();
    Flux<Blog> findAllByAuthorId(Blog blog);
    Mono<Blog> save(Blog blog);
    Flux<Void> delete(String id);
    Flux<Void> borrarBlogbyAuthorId(String authorId);
    Mono<Long> countByAuthorIdAndStatus(String authorId, String status);
    Mono<Blog> update(Blog blog);
    Flux<Blog> findByParam(String status, String authorId);
    Mono<Blog> inactivarBlog(String id);
}
