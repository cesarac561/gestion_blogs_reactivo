package com.bootcamp.reactive.blog.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Reaction {
    private String id;
    private String type;
    private Date date;
    private String userId;


}
