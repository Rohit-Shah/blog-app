package com.blog.blog.mapper.PostMapper;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.entity.PostEntity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostDTO toDTO(Post post);
    Post toEntity(PostDTO postDTO);

}
