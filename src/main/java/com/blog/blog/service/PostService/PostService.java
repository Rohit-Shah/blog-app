package com.blog.blog.service.PostService;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.DTO.PostRequest.PostSearchCriteria;
import com.blog.blog.Response.CursorResponse;
import com.blog.blog.Response.PageResponse;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import org.springframework.data.domain.Page;

import java.util.List;


public interface PostService {
    //add
    PostDTO addPost(UserPrincipal userPrincipal,PostDTO postRequest,String imageUrl);
    PostDTO publishPost(UserPrincipal userPrincipal,Long postId);
    //update
    PostDTO updatePost(UserPrincipal userPrincipal,Long postId,PostDTO updatedPost,String imageUrl) ;
    //delete
    void deletePost(UserPrincipal userPrincipal,Long postId);

    //get
    PostDTO getPostById(Long postId);
    PostDTO getUserPostById(Long userId,Long postId);
    CursorResponse<PostDTO> getAllPosts(int size, PostSearchCriteria criteria);
    PostDTO getPostBySlug(String slug);
}
