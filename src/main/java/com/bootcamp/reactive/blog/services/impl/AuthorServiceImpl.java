package com.bootcamp.reactive.blog.services.impl;

import com.bootcamp.reactive.blog.core.exception.AuthorExistsException;
import com.bootcamp.reactive.blog.core.exception.AuthorNotFoundException;
import com.bootcamp.reactive.blog.entities.Author;
import com.bootcamp.reactive.blog.repositories.AuthorRepository;
import com.bootcamp.reactive.blog.services.AuthorService;
import com.bootcamp.reactive.blog.services.BlogService;
import com.bootcamp.reactive.blog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Override
    public Mono<Author> findById(String id) {
        return this.authorRepository.findById(id);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return authorRepository.existsByEmail(email);
    }

    @Override
    public Flux<Author> findByEmail(String email) {
//        var authorFilter = new Author();
//        authorFilter.setEmail(email);
//
//        return this.authorRepository.findAll(Example.of(authorFilter));

        return this.authorRepository.findByEmail(email);
    }

    @Override
    public Flux<Author> findByName(String name) {
        return this.authorRepository.findByName(name);
    }

    @Override
    public Flux<Author> findAll() {
        return this.authorRepository.findAll();
    }

    @Override
    public Mono<Author> save(Author author) {
        return this.authorRepository.save(author);
    }

    @Override
    public Mono<Author> update(Author author) {

        return this.authorRepository.findById(author.getId())
                .flatMap(a -> {
                    a.setName(author.getName());
                    a.setEmail(author.getEmail());
                    a.setPhone(author.getPhone());
                    a.setBirthDate(author.getBirthDate());
                    return this.authorRepository.save(a);
                })
                .switchIfEmpty(Mono.error(new AuthorNotFoundException("Autor no existe")));
    }

    @Override
    public Mono<Author> saveWithValidation(Author author) {

//        return this.authorRepository.existsByEmail(author.getEmail())
//                .flatMap(exists->
//                        {
//                            return exists ? Mono.empty():this.authorRepository.save(author);
//                        });

        return this.authorRepository.existsByEmail(author.getEmail())
                .flatMap(exists->
                {
                    return !exists ? this.authorRepository.save(author): Mono.error(new AuthorExistsException("Existe autor con el mismo email"));
                });

    }

    @Override
    public Flux<Void> delete(String id) {

        //Regla 8: Al eliminar un autor, debe realizarse una eliminación en cadena de sus blogs, post y comentarios.
        //Se asume que también el usuario, para que no quede "huérfano" el registro
        return this.findById(id)
                .flatMap(author -> this.authorRepository.delete(author))
                .then(this.userService.borrarUserbyAuthorId(id))
                .thenMany(this.blogService.borrarBlogbyAuthorId(id));

    }

}
