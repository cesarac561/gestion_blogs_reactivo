package com.bootcamp.reactive.blog.handlers;

import com.bootcamp.reactive.blog.entities.*;
import com.bootcamp.reactive.blog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class PostHandler {

    @Autowired
    private PostService postService;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ok().contentType(APPLICATION_JSON)
                .body(postService.findAll(), Post.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        return this.postService.findById(request.pathVariable("id"))
                .flatMap(post -> ServerResponse.ok().body(Mono.just(post), Post.class))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> findByBlogId(ServerRequest request){
        var blogId=request.queryParam("blogId").get();

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(postService.findByBlogId(blogId), Post.class);

    }

    public Mono<ServerResponse> save(ServerRequest request) {

        return request.bodyToMono(Post.class)
                .flatMap(post -> this.postService.save(post))
                .flatMap(post -> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(post), Post.class));
    }

    public Mono<ServerResponse> update(ServerRequest request){

        var postInput= request.bodyToMono(Post.class);

        return postInput
                .flatMap(p -> this.postService.update(p))
                .flatMap(post -> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(post), Post.class));

    }


    public Mono<ServerResponse> publicarPost(ServerRequest request) {
        String postId = request.pathVariable("id");

        return this.postService.publicarPost(postId)
                .flatMap(post -> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(post), Post.class));

    }

    public Mono<ServerResponse> registrarComentario(ServerRequest request) {
        String postId = request.pathVariable("id");
        var comentarioInput = request.bodyToMono(Comment.class);

        //Regla 10: Un usuario puede comentar n veces en cada post.
        //No se valida la cantidad de comentarios que puede generar el usuario por la regla 10
        return comentarioInput
                .flatMap(comentario -> this.postService.registrarComentario(postId, comentario))
                .flatMap(post -> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(post), Post.class));
    }

    public Mono<ServerResponse> registrarReaccion(ServerRequest request) {
        String postId = request.pathVariable("id");
        var reaccionInput= request.bodyToMono(Reaction.class);

        return reaccionInput
                .flatMap(reaccion -> {
                    reaccion.setType("like");
                    return this.postService.registrarReaccion(postId,reaccion);
                })
                .flatMap(post -> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(post), Post.class));

    }

    public Mono<ServerResponse> quitarReaccion(ServerRequest request) {
        String postId = request.pathVariable("id");
        var idUsuarioInput= request.bodyToMono(String.class);

        return idUsuarioInput
                .flatMap(idUsuario -> this.postService.quitarReaccion(postId,idUsuario))
                .flatMap(post -> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(post), Post.class));

    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        return this.postService.delete(request.pathVariable("id"))
                .then(ServerResponse.noContent().build());
    }

}
