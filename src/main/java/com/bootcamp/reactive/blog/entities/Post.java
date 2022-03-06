package com.bootcamp.reactive.blog.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(value="posts")
public class Post {
    @Id
    private String id;
    private String title;
    private Date date;
    private String status;
    private String content;
    private String blogId;
    private List<Comment> comments = new ArrayList<>();;
    private List<Reaction> reactions = new ArrayList<>();
}
