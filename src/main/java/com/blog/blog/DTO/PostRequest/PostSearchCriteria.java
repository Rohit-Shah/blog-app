package com.blog.blog.DTO.PostRequest;

import com.blog.blog.constants.PostContants.PostCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchCriteria {
    private String content;
    private PostCategory category;
    private Long userId;
    private String cursor;
}
