package com.bootcamp.reactive.blog.services;

import com.bootcamp.reactive.blog.entities.Comment;
import com.bootcamp.reactive.blog.entities.Post;
import com.bootcamp.reactive.blog.entities.Reaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface PostService {
    Flux<Post> findAll();
    Mono<Post> findById(String id);
    Flux<Post> findByBlogId(String blogId);
    Mono<Boolean> existsByBlogIdAndStatusAndDate(String blogId, String status, Date date);
    Mono<Post> save(Post post);
    Mono<Post> update(Post post);
    Mono<Post> publicarPost(String postId);
    Mono<Post> registrarComentario(String postId, Comment comentario);
    Mono<Post> registrarReaccion(String postId, Reaction reaccion);
    Mono<Post> quitarReaccion(String postId, String idUsuario);
    Mono<Void> delete(String id);
    Flux<Void> borrarPostbyBlogId(String blogId);
}
