package com.blog.blog.mapper.CommentMapper;

import com.blog.blog.DTO.CommentReqeust.CommentDTO;
import com.blog.blog.entity.CommentEntity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDTO toDTO(Comment comment);
    Comment toEntity(CommentDTO commentDTO);
}
