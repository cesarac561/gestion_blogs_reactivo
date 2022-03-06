package com.bootcamp.reactive.blog.services.impl;

import com.bootcamp.reactive.blog.core.exception.*;
import com.bootcamp.reactive.blog.entities.*;
import com.bootcamp.reactive.blog.repositories.BlogRepository;
import com.bootcamp.reactive.blog.repositories.PostRepository;
import com.bootcamp.reactive.blog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Flux<Post> findAll() {
        return this.postRepository.findAll();
    }

    @Override
    public Mono<Post> findById(String id) {
        return  this.postRepository.findById(id);
    }

    @Override
    public Flux<Post> findByBlogId(String blogId) {
//        var postFilter = new Post();
//        postFilter.setBlogId(blogId);
//
//        return this.postRepository.findAll(Example.of(postFilter));

        return this.postRepository.findByBlogId(blogId);
    }

    @Override
    public Mono<Boolean> existsByBlogIdAndStatusAndDate(String blogId, String status, Date date) {
        return postRepository.existsByBlogIdAndStatusAndDate(blogId, status, date);
    }

    @Override
    public Mono<Post> save(Post post) {
        return this.blogRepository.findById(post.getBlogId())
                .flatMap(b -> {
                    //Regla 6: Solo se puede registrar posts en blogs en estado activo.
                    if(b.getStatus().equalsIgnoreCase("activo")){
                        post.setStatus("borrador");
                        return this.postRepository.save(post);
                    }
                    return Mono.error(new PostNotFoundException("Blog no está activo"));
                })
                .switchIfEmpty(Mono.error(new PostNotFoundException("Blog no existe")));

    }

    @Override
    public Mono<Post> update(Post post) {

        return  this.findById(post.getId())
                .flatMap(p -> this.blogRepository.findById(post.getBlogId())
                                    .map(b -> Pair.of(p,b))
                                    .switchIfEmpty(Mono.error(new PostNotFoundException("Blog no existe")))
                )
                .flatMap(PostBlogPair -> {
                    var p = PostBlogPair.getFirst();
                    var b = PostBlogPair.getSecond();

                    if(b.getStatus().equalsIgnoreCase("activo")) {
                        p.setTitle(post.getTitle());
                        p.setDate(post.getDate());
                        //Se asume que no se puede modificar el estado.
                        //Debe hacerse desde el endpoint publicar post.
                        p.setContent(post.getContent());
                        p.setBlogId(post.getBlogId());
                        //Se asume que no se pueden modificar los comentarios ni las reacciones.
                        //Debe hacer desde los endpoints registrar comentarios y registrar reacciones.
                        return this.postRepository.save(p);
                    }
                    return Mono.error(new PostNotFoundException("Blog no está activo"));
                })
                .switchIfEmpty(Mono.error(new PostNotFoundException("Post no existe")));
    }

    @Override
    public Mono<Post> publicarPost(String postId) {

        Date fechaHoy = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        return this.findById(postId)
                .flatMap(p -> this.existsByBlogIdAndStatusAndDate(p.getBlogId(), "publicado", fechaHoy)
                                    .map(existe -> Pair.of(p, existe)))
                .flatMap(PostExistePair -> {
                    var existe = PostExistePair.getSecond();
                    var post =PostExistePair.getFirst();
                    //Regla 5: Solo se puede publicar un post por día.
                    if(!existe){
                        post.setStatus("publicado");
                        post.setDate(fechaHoy);
                        return this.postRepository.save(post);
                    }
                    return Mono.error(new PostExistsException("Solo se puede publicar un post por día."));
                })
                .switchIfEmpty(Mono.error(new PostNotFoundException("Post no existe")));

    }

    //Regla 7: Solo se pueden registrar comentarios en post en estado publicado.
    //Regla 10: Un usuario puede comentar n veces en cada post.
    //No se valida la cantidad de comentarios que puede generar el usuario por la regla 10
    @Override
    public Mono<Post> registrarComentario(String postId, Comment comentario) {

       return this.findById(postId)
                    .flatMap(p -> {
                        if(p.getStatus().equalsIgnoreCase("publicado")) {
                            List<Comment> comments = p.getComments();
                            comments.add(comentario);
                            p.setComments(comments);
                            return this.postRepository.save(p);
                        }
                        return Mono.error(new PostExistsException("Solo se pueden registrar comentarios en post en estado publicado."));
                    })
                    .switchIfEmpty(Mono.error(new PostNotFoundException("Post no existe")));

    }

    @Override
    public Mono<Post> registrarReaccion(String postId, Reaction reaccion) {

        return this.findById(postId)
                .flatMap(p -> {
                    //Ya que los comentarios solo se pueden registrar en posts en estado publicado, se asume
                    //que es el mismo caso para las reacciones
                    if(p.getStatus().equalsIgnoreCase("publicado")) {
                        List<Reaction> reacciones = p.getReactions();
                        //Regla 9: Un usuario solo puede tener una reacción para cada post. También puede quitarle la reacción.
                        if(!reacciones.isEmpty())
                            if(existeReaccionDeUsuario(reacciones, reaccion))
                                return Mono.error(new PostExistsException("Un usuario solo puede tener una reacción para cada post."));
                        reacciones.add(reaccion);
                        p.setReactions(reacciones);
                        return this.postRepository.save(p);

                    }
                    return Mono.error(new PostExistsException("Solo se pueden registrar reacciones en post en estado publicado."));
                })
                .switchIfEmpty(Mono.error(new PostNotFoundException("Post no existe")));

    }

    @Override
    public Mono<Post> quitarReaccion(String postId, String idUsuario) {

        return this.findById(postId)
                .flatMap(p -> {
                    //Ya que asumimos que las reacciones solo se pueden registrar en posts en estado publicado, se asume
                    //que es el mismo caso para quitar una reacción
                    if(p.getStatus().equalsIgnoreCase("publicado")) {                    
                        List<Reaction> reacciones = p.getReactions();
                        //Regla 9: Un usuario solo puede tener una reacción para cada post. También puede quitarle la reacción.
                        if(!reacciones.isEmpty())
                            return buscarReaccion(reacciones, idUsuario)
                                    .map(indReaccion -> Pair.of(p, indReaccion));
                        return Mono.error(new PostExistsException("El post no tiene reacciones"));
                    }
                    return Mono.error(new PostExistsException("Solo se pueden quitar reacciones en post en estado publicado."));

                })
                .flatMap(PostIndPair -> {
                    var indReaccion = PostIndPair.getSecond();
                    var post =PostIndPair.getFirst();
                    if(indReaccion>=0){
                       List<Reaction> reacciones = post.getReactions();
                       reacciones.remove(reacciones.get(indReaccion));
                       post.setReactions(reacciones);
                       return this.postRepository.save(post);
                    }
                    return Mono.error(new PostExistsException("No existe reacción para el usuario"));
                })
                .switchIfEmpty(Mono.error(new PostNotFoundException("Post no existe")));

    }

    public boolean existeReaccionDeUsuario(List<Reaction> reacciones, Reaction reaccion) {
        return reacciones
                .stream()
                .anyMatch(r -> r.getUserId().equalsIgnoreCase(reaccion.getUserId()));

    }

    public Mono<Integer> buscarReaccion(List<Reaction> reacciones, String idUsuario) {
        Integer index = IntStream.range(0, reacciones.size())
                .filter(reaccionInd -> reacciones.get(reaccionInd).getUserId().equalsIgnoreCase(idUsuario))
                .findFirst().orElse(-1);
        return Mono.just(index);
    }

    @Override
    public Mono<Void> delete(String id) {

        return this.findById(id)
                .flatMap(post -> this.postRepository.delete(post));

    }

    @Override
    public Flux<Void> borrarPostbyBlogId(String blogId){

        return this.findByBlogId(blogId)
                .flatMap(post -> this.delete(post.getId()));

    }


}
