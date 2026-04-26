package com.blog.blog.repository.Cursor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class Cursor {

    private Date createdAt;
    private Long id;

}
