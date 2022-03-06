package com.bootcamp.reactive.blog.handlers;

import com.bootcamp.reactive.blog.entities.Blog;
import com.bootcamp.reactive.blog.services.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class BlogHandler {
    @Autowired
    private BlogService blogService;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ok().contentType(APPLICATION_JSON)
                .body(blogService.findAll(), Blog.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        return this.blogService.findById(request.pathVariable("id"))
                .flatMap(blog -> ServerResponse.ok().body(Mono.just(blog), Blog.class))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> findByParam(ServerRequest request){
        var statusOpt = request.queryParam("status");
        var authorIdOpt = request.queryParam("authorId");
        String status, authorId;

        if(statusOpt.isPresent())
            status = statusOpt.get();
        else
            status = null;

        if(authorIdOpt.isPresent())
            authorId = authorIdOpt.get();
        else
            authorId = null;

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(blogService.findByParam(status, authorId), Blog.class);

    }

    public Mono<ServerResponse> save(ServerRequest request) {

        return request.bodyToMono(Blog.class)
                .flatMap(blog -> this.blogService.save(blog))
                .flatMap(blog -> ServerResponse
                .ok()
                .contentType(APPLICATION_JSON)
                .body(Mono.just(blog), Blog.class));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        return this.blogService.delete(request.pathVariable("id"))
                .then(ServerResponse.noContent().build());
    }


    public Mono<ServerResponse> update(ServerRequest request){

        var blogInput= request.bodyToMono(Blog.class);

        return blogInput
                .flatMap(b -> this.blogService.update(b))
                .flatMap(b-> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(b), Blog.class));

    }

    public Mono<ServerResponse> inactivarBlog(ServerRequest request) {
        String blogId = request.pathVariable("id");

        return this.blogService.inactivarBlog(blogId)
                .flatMap(b-> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(b), Blog.class));

    }


}
