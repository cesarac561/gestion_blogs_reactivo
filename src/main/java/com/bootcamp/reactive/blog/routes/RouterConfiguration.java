package com.bootcamp.reactive.blog.routes;

import com.bootcamp.reactive.blog.handlers.AuthorHandler;
import com.bootcamp.reactive.blog.handlers.BlogHandler;
import com.bootcamp.reactive.blog.handlers.PostHandler;
import com.bootcamp.reactive.blog.handlers.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;

@Configuration
public class RouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> blogRoutes(BlogHandler blogHandler) {
        return RouterFunctions.nest(RequestPredicates.path("/blogs"),
                RouterFunctions
                        .route(GET(""), blogHandler::findAll)
                        .andRoute(GET("/query"), blogHandler::findByParam)
                        .andRoute(GET("/{id}"), blogHandler::findById)
                        .andRoute(POST("").and(contentType(APPLICATION_JSON)), blogHandler::save)
//						.andRoute(PUT("/{id}").and(contentType(APPLICATION_JSON)), blogHandler::update)
                        .andRoute(PUT("").and(accept(APPLICATION_JSON)),blogHandler::update)
                        .andRoute(PATCH("/{id}").and(accept(APPLICATION_JSON)),blogHandler::inactivarBlog)
                        .andRoute(DELETE("/{id}"), blogHandler::delete)
            );
    }

    @Bean
    public RouterFunction<ServerResponse> authorRoutes(AuthorHandler authorHandler){
        return RouterFunctions.nest(RequestPredicates.path("/authors"),
                RouterFunctions
                .route(GET(""), authorHandler::findAll)
                .andRoute(GET("/by-email/{email}"), authorHandler::findByEmail)
                .andRoute(GET("/query"), authorHandler::findByEmail)
//                .andRoute(GET("/query/{email}"), authorHandler::findByEmail)
                .andRoute(GET("/{id}"), authorHandler::findById)
                .andRoute(POST("").and(accept(APPLICATION_JSON)),authorHandler::save)
                .andRoute(PUT("").and(accept(APPLICATION_JSON)),authorHandler::update)
                .andRoute(DELETE("/{id}"), authorHandler::delete)
            );
    }

    @Bean
    public RouterFunction<ServerResponse> postRoutes(PostHandler postHandler){
        return RouterFunctions.nest(RequestPredicates.path("/posts"),
                RouterFunctions
                        .route(GET(""), postHandler::findAll)
                        .andRoute(GET("/query"), postHandler::findByBlogId)
                        .andRoute(GET("/{id}"), postHandler::findById)
                        .andRoute(POST("").and(accept(APPLICATION_JSON)),postHandler::save)
                        .andRoute(PUT("").and(accept(APPLICATION_JSON)),postHandler::update)
                        .andRoute(PATCH("/publicar_post/{id}").and(accept(APPLICATION_JSON)),postHandler::publicarPost)
                        .andRoute(PATCH("/registrar_comentario/{id}").and(accept(APPLICATION_JSON)),postHandler::registrarComentario)
                        .andRoute(PATCH("/registrar_reaccion/{id}").and(accept(APPLICATION_JSON)),postHandler::registrarReaccion)
                        .andRoute(PATCH("/quitar_reaccion/{id}").and(accept(APPLICATION_JSON)),postHandler::quitarReaccion)
                        .andRoute(DELETE("/{id}"), postHandler::delete)
        );
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler){
        return RouterFunctions.nest(RequestPredicates.path("/users"),
                RouterFunctions
                        .route(GET(""), userHandler::findAll)
                        .andRoute(GET("/query"), userHandler::autenticarUsuario)
                        .andRoute(GET("/busqueda_por_autor/{id}"), userHandler::findByAuthorId)
                        .andRoute(GET("/{id}"), userHandler::findById)
                        .andRoute(POST("").and(accept(APPLICATION_JSON)),userHandler::save)
                        .andRoute(PUT("").and(accept(APPLICATION_JSON)),userHandler::update)
                        .andRoute(DELETE("/{id}"), userHandler::delete)
        );
    }
}
