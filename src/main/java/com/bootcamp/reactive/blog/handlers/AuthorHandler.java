package com.bootcamp.reactive.blog.handlers;

import com.bootcamp.reactive.blog.entities.Author;
import com.bootcamp.reactive.blog.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class AuthorHandler {

    @Autowired
    private AuthorService authorService;

    public Mono<ServerResponse> findAll(ServerRequest request){
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(authorService.findAll(), Author.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request){
        var id = request.pathVariable("id");
//        return ServerResponse.ok()
//                .contentType(APPLICATION_JSON)
//                .body(authorService.findById(id), Author.class);

        return this.authorService.findById(id)
                .flatMap(a-> ServerResponse.ok().body(Mono.just(a), Author.class))
                .switchIfEmpty(ServerResponse.notFound().build());

    }


    public Mono<ServerResponse> findByEmail(ServerRequest request){
        var email=request.queryParam("email").get();

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(authorService.findByEmail(email), Author.class);

    }


    public Mono<ServerResponse> save(ServerRequest request){

        var authorInput= request.bodyToMono(Author.class);

//        return authorInput
//                .flatMap(a-> {
//                    return authorService.findByEmail(a.getEmail())
//                            .hasElements()
//                            .flatMap(exists-> !exists ?  authorService.save(a): Mono.empty());
//                })
//                .switchIfEmpty(Mono.error(new AuthorExistsException("Author exists")))
//                .flatMap(a-> ServerResponse
//                        .ok()
//                        .contentType(APPLICATION_JSON)
//                        .body(Mono.just(a), Author.class));




//        return authorInput
//                .filterWhen(a-> {
//                    return authorService.findByEmail(a.getEmail())
//                            .hasElements()
//                            .map(success-> !success);
//                })
//                .flatMap(a-> {
//                    return authorService.save(a);
//                })
//                .switchIfEmpty(Mono.error(new AuthorExistsException("Author exists")))
//                .flatMap(a-> {
//                    return ServerResponse
//                            .ok()
//                            .contentType(APPLICATION_JSON)
//                            .body(Mono.just(a), Author.class);
//                });


//
//        return authorInput
//                .flatMap(a-> {
//                    return authorService.existsByEmail(a.getEmail())
//                            .flatMap(exists-> exists ? Mono.empty():  authorService.save(a));
//                })
//                .switchIfEmpty(Mono.error(new AuthorExistsException("Author exists")))
//                .flatMap(a-> ServerResponse
//                        .ok()
//                        .contentType(APPLICATION_JSON)
//                        .body(Mono.just(a), Author.class));


        return authorInput
                .flatMap(author-> this.authorService.save(author))
                .flatMap(a-> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(a), Author.class));
//                .switchIfEmpty(Mono.error(new AuthorExistsException("Author exists")));

    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String authorId = serverRequest.pathVariable("id");

        return this.authorService.delete(serverRequest.pathVariable("id"))
                .then(ServerResponse.ok().build());

//
//        return ServerResponse.ok()
//                .contentType(APPLICATION_JSON)
//                .body(authorService.delete(authorId),Author.class);

    }

    public Mono<ServerResponse> update(ServerRequest request){

        var authorInput= request.bodyToMono(Author.class);

        return authorInput
                .flatMap(a -> this.authorService.update(a))
                .flatMap(a-> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(a), Author.class));

    }

}
