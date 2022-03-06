package com.bootcamp.reactive.blog.services.impl;

import com.bootcamp.reactive.blog.core.exception.*;
import com.bootcamp.reactive.blog.entities.User;
import com.bootcamp.reactive.blog.repositories.AuthorRepository;
import com.bootcamp.reactive.blog.repositories.UserRepository;
import com.bootcamp.reactive.blog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public Flux<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public Mono<User> findById(String id) {
        return  this.userRepository.findById(id);
    }

    @Override
    public Mono<User> findByAuthorId(String authorId) {
//        var userFilter = new User();
//        userFilter.setAuthorId(authorId);
//
//        return this.userRepository.findAll(Example.of(userFilter));

        return this.userRepository.findByAuthorId(authorId);
    }


    @Override
    public Mono<Boolean> existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    @Override
    public Mono<Boolean> existsByAuthorId(String authorId) {
        return userRepository.existsByAuthorId(authorId);
    }

    //Regla 1: El usuario debe autenticarse.
    @Override
    public Mono<User> autenticarUsuario(String login, String password) {

        return this.userRepository.findByLoginAndPassword(login, password)
                .switchIfEmpty(Mono.error(new UserNotAuthenticatedException("Autenticación fallida. Login y password no existen.")));

    }

    @Override
    public Mono<User> save(User user) {
        return this.userRepository.save(user);
    }

    //Regla 2: Un autor debe tener solo un usuario.
    @Override
    public Mono<User> saveWithValidation(User user) {

        return this.existsByLogin(user.getLogin())
                .flatMap(existsLoginUser-> {
                    if(!existsLoginUser) {
                        if (!(user.getAuthorId()==null || user.getAuthorId().isEmpty()))
                            return this.existsByAuthorId(user.getAuthorId());
                        return Mono.just(Boolean.FALSE);
                    }
                    else
                        return Mono.error(new UserExistsException("El login del usuario ya existe"));
                })
                .flatMap(existsAuthorIdUser -> {
                    if(!existsAuthorIdUser){
                        if (!(user.getAuthorId()==null || user.getAuthorId().isEmpty()))
                            return this.authorRepository.existsById(user.getAuthorId());
                        return Mono.just(Boolean.TRUE);
                    }
                    return Mono.error(new UserExistsException("Autor ya tiene un usuario"));
                })
                .flatMap(existsAuthor -> {
                    return (existsAuthor) ? this.userRepository.save(user) : Mono.error(new UserExistsException("Autor no existe."));
                });

    }

    @Override
    public Mono<User> update(User user) {

        //Regla 2: Un autor debe tener solo un usuario.
        return this.findById(user.getId())
                .flatMap(u -> {
                    if (!(user.getAuthorId()==null || user.getAuthorId().isEmpty())) {
                        if (!u.getAuthorId().equalsIgnoreCase(user.getAuthorId()))
                            return this.existsByAuthorId(user.getAuthorId());
                        return Mono.just(Boolean.FALSE);
                    }
                    return Mono.just(Boolean.FALSE);
                })
                .flatMap(existsAuthorIdUser -> {
                    if(!existsAuthorIdUser){
                        if (!(user.getAuthorId()==null || user.getAuthorId().isEmpty()))
                            return this.authorRepository.existsById(user.getAuthorId());
                        return Mono.just(Boolean.TRUE);
                    }
                    return Mono.error(new UserExistsException("Autor ya tiene un usuario"));
                })
                .flatMap(existsAuthor -> {
                    return (existsAuthor) ? this.findById(user.getId()) : Mono.error(new UserExistsException("Autor no existe."));
                })
                .flatMap(u -> {
                    //Se asume que el login no se puede cambiar
                    u.setPassword(user.getPassword());
                    u.setAuthorId(user.getAuthorId());
                    return this.userRepository.save(u);
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario no existe")));

    }

    @Override
    public Mono<Void> delete(String id) {

        return this.findById(id)
                .flatMap(user -> this.userRepository.delete(user));

    }

    @Override
    public Mono<Void> borrarUserbyAuthorId(String authorId){

        return this.findByAuthorId(authorId)
                .flatMap(user -> this.delete(user.getId()));

    }
}
