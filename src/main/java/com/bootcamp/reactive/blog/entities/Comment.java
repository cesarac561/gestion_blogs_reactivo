package com.bootcamp.reactive.blog.entities;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private String id;
    private Date date;
    private String status;
    private String comment;
    private String userId;


}
