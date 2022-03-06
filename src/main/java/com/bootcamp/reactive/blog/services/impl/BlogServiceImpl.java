package com.bootcamp.reactive.blog.services.impl;


import com.bootcamp.reactive.blog.core.exception.BlogExistsException;
import com.bootcamp.reactive.blog.core.exception.BlogNotFoundException;
import com.bootcamp.reactive.blog.entities.Blog;
import com.bootcamp.reactive.blog.repositories.AuthorRepository;
import com.bootcamp.reactive.blog.repositories.BlogRepository;
import com.bootcamp.reactive.blog.services.BlogService;
import com.bootcamp.reactive.blog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;


@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PostService postService;

    @Override
    public Mono<Blog> findById(String id) {
        return  this.blogRepository.findById(id);
    }

    @Override
    public Flux<Blog> findAll() {
        return blogRepository.findAll();
    }

    @Override
    public Flux<Blog> findAllByAuthorId(Blog blog) {

        return blogRepository.findAll(Example.of(blog));
    }

    @Override
    public Mono<Blog> save(Blog blog) {

        blog.setStatus("activo");

        //Regla 3: Solo pueden tener blogs los autores mayores de 18 años.
        //Regla 4: Un autor puede tener máximo 03 blogs.
        //Se está asumiendo que una vez que se inactiva un blog no se puede volver a activar
        //Por lo tanto hacemos la validación de los 3 blogs, solo con blogs activos
        return this.authorRepository.findById(blog.getAuthorId())
                .flatMap(a -> this.obtenerEdad(a.getBirthDate()))
                .flatMap(edadAuthor -> {
                    return (edadAuthor >= 18) ? this.countByAuthorIdAndStatus(blog.getAuthorId(),"activo") : Mono.error(new BlogExistsException("Autor no es mayor de 18 años"));
                })
                .flatMap(nroBlogs -> {
                    return (nroBlogs < 3L) ? this.blogRepository.save(blog): Mono.error(new BlogExistsException("Autor ya tiene 3 blogs"));
                });
    }

    @Override
    public Mono<Blog> update(Blog blog) {

        //Regla 3: Solo pueden tener blogs los autores mayores de 18 años.
        return   this.authorRepository.findById(blog.getAuthorId())
                .flatMap(a -> this.obtenerEdad(a.getBirthDate()))
                .flatMap(edadAuthor -> {
                    return (edadAuthor >= 18) ? this.findById(blog.getId()) : Mono.error(new BlogExistsException("Autor no es mayor de 18 años"));
                })
                .flatMap(b -> {
                    b.setName(blog.getName());
                    b.setAuthorId(blog.getAuthorId());
                    b.setUrl(blog.getUrl());
                    //Se asume que si el status del blog es inactivo no se puede volver a activar
                    //Por lo tanto solo se puede modificar si está activo
                    if(b.getStatus().equalsIgnoreCase("activo"))
                        b.setStatus(blog.getStatus());
                    return this.blogRepository.save(b);
                })
                .switchIfEmpty(Mono.error(new BlogNotFoundException("Blog no existe")));
    }

    @Override
    public Flux<Void> delete(String id) {

        //Regla 8: Al eliminar un autor, debe realizarse una eliminación en cadena de sus blogs, post y comentarios.
        //Se asume que al eliminar un blog también debe eliminar todos sus hijos
        return this.findById(id)
                .flatMap(blog -> this.blogRepository.delete(blog))
                .thenMany(this.postService.borrarPostbyBlogId(id));

    }

    @Override
    public Flux<Void> borrarBlogbyAuthorId(String authorId){

        return this.findByParam(null,authorId)
                .flatMap(blog -> this.delete(blog.getId()));

    }


    @Override
    public Mono<Long> countByAuthorIdAndStatus(String authorId, String status) {
        return blogRepository.countByAuthorIdAndStatus(authorId, status);
    }

    public Mono<Integer> obtenerEdad(Date birthdate) {
        return Mono.just(Period.between(birthdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears());
    }

    @Override
    public Flux<Blog> findByParam(String status, String authorId) {
        var blogFilter = new Blog();

        if(authorId==null) {

            blogFilter.setStatus(status);


        } else if(status==null) {
            blogFilter.setAuthorId(authorId);
        } else {
            blogFilter.setStatus(status);
            blogFilter.setAuthorId(authorId);
        }

        return this.blogRepository.findAll(Example.of(blogFilter));

    }

    @Override
    public Mono<Blog> inactivarBlog(String id) {

        return  this.findById(id)
                .flatMap(b -> {
                    b.setStatus("inactivo");
                    return this.blogRepository.save(b);
                })
                .switchIfEmpty(Mono.error(new BlogNotFoundException("Blog no existe")));
    }

}
