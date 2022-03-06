package com.bootcamp.reactive.blog.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorNotFoundException extends RuntimeException{
    private HttpStatus status  = HttpStatus.NOT_FOUND;
    private String message;

    public AuthorNotFoundException(String message){
        this.message = message;
    }
}
