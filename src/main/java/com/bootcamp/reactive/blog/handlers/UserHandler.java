package com.bootcamp.reactive.blog.handlers;

import com.bootcamp.reactive.blog.entities.Blog;
import com.bootcamp.reactive.blog.entities.User;
import com.bootcamp.reactive.blog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class UserHandler {

    @Autowired
    private UserService userService;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ok().contentType(APPLICATION_JSON)
                .body(userService.findAll(), User.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        return this.userService.findById(request.pathVariable("id"))
                .flatMap(user -> ServerResponse.ok().body(Mono.just(user), User.class))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> findByAuthorId(ServerRequest request){
        var authorId=request.pathVariable("id");

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(userService.findByAuthorId(authorId), User.class);

    }

    public Mono<ServerResponse> autenticarUsuario(ServerRequest request){
        var loginOpt = request.queryParam("login");
        var passwordOpt = request.queryParam("password");
        String login, password;

        if (!loginOpt.isPresent() || !passwordOpt.isPresent()) return badRequest().build();

        login = loginOpt.get();
        password = passwordOpt.get();

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(userService.autenticarUsuario(login, password), Blog.class);

    }

    public Mono<ServerResponse> save(ServerRequest request) {

        var userInput= request.bodyToMono(User.class);

        return request.bodyToMono(User.class)
                .flatMap(user -> this.userService.saveWithValidation(user))
                .flatMap(user -> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(user), User.class));
    }


    public Mono<ServerResponse> update(ServerRequest request){

        var userInput= request.bodyToMono(User.class);

        return userInput
                .flatMap(u -> this.userService.update(u))
                .flatMap(u-> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(u), User.class));

    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        return this.userService.delete(request.pathVariable("id"))
                .then(ServerResponse.noContent().build());
    }

}
