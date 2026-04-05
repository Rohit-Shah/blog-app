package com.blog.blog.service.serviceBean.PostServiceBean;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.Exceptions.AuthExceptions.UnauthorizedException;
import com.blog.blog.Exceptions.PostExceptions.PostNotFoundException;
import com.blog.blog.annotations.LogUserAction;
import com.blog.blog.config.AppProperties;
import com.blog.blog.constants.PostContants.PostStatus;
import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.mapper.PostMapper.PostMapper;
import com.blog.blog.repository.PostRepository.PostRepository;
import com.blog.blog.repository.UserRepository.UserRepository;
import com.blog.blog.service.PostService.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceBean implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    @Override
    @LogUserAction(actionType = "CREATE_POST")
    @Transactional
    public PostDTO addPost(UserPrincipal userPrincipal, PostDTO postRequest, String imageUrl) {
        User user = userPrincipal.getUser();
        if(user == null){
            throw new UsernameNotFoundException("No such user found");
        }
        Post currPost = postMapper.toEntity(postRequest);
        //set other post information
        currPost.setUser(user);
        //generate slug
        String postSlug = getPostSlug(postRequest.getTitle());
        currPost.setSlug(postSlug);
        //no image for now
        if(imageUrl != null){
            currPost.setImageUrl(imageUrl);
        }
        //set post status
        currPost.setStatus(postRequest.getPostStatus() != null ? postRequest.getPostStatus() : PostStatus.DRAFT);
        return postMapper.toDTO(postRepository.save(currPost));
    }

    @Override
    @Transactional
    public PostDTO publishPost(UserPrincipal userPrincipal,Long postId){
        User user = userPrincipal.getUser();
        Post post = getOwnedPost(postId,user.getUserId());
        if(post.getStatus().equals(PostStatus.PUBLISHED)){
            throw new IllegalStateException("Post is already published");
        }
        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishedAt(new Date());
        return postMapper.toDTO(postRepository.save(post));
    }

    private Post getOwnedPost(Long postId,Long userId){
        Post dbPost = postRepository.findPostByPostId(postId).orElseThrow(() -> new PostNotFoundException("No such post found"));
        if(!Objects.equals(dbPost.getUser().getUserId(), userId)){
            throw new UnauthorizedException("You are not authorized to update this post");
        }
        return dbPost;

    }

    private String getPostSlug(String postTitle){
        String postSlug = "";
        postSlug = postTitle.toLowerCase().replaceAll(" ","-");
        int counter = 0;
        String baseSlug = postSlug;
        while(postRepository.findPostBySlug(postSlug) != null){
            postSlug = baseSlug + "-" + counter;
            counter++;
        }
        return postSlug;
    }

    @Override
    @LogUserAction(actionType = "GET_POST")
    public PostDTO getPostById(Long postId) {
        Optional<Post> userPost = postRepository.findPostByPostId(postId);
        if(userPost.isEmpty()){
            throw new PostNotFoundException("No such post found");
        }
        return postMapper.toDTO(userPost.get());
    }

    @Override
    @LogUserAction(actionType = "UPDATE_POST")
    @PreAuthorize(
            "hasPermission(#postId,'Post','CAN_EDIT_OWN_POST') or hasAuthority('CAN_EDIT_ANY_POST')"
    )
    public PostDTO updatePost(UserPrincipal userPrincipal, Long postId, PostDTO updatedPost, String imageUrl)  {

        User user = userPrincipal.getUser();

        Post dbPost = postRepository.findPostByPostId(postId)
                .orElseThrow(() -> new PostNotFoundException("No such post found"));

        if (updatedPost.getTitle() != null && !updatedPost.getTitle().isBlank()) {
            dbPost.setTitle(updatedPost.getTitle());
        }

        if (updatedPost.getContent() != null && !updatedPost.getContent().isBlank()) {
            dbPost.setContent(updatedPost.getContent());
        }

        if(imageUrl != null){
            dbPost.setImageUrl(imageUrl);
        }

        Post savedPost = postRepository.save(dbPost);
        return postMapper.toDTO(savedPost);
    }

    @Override
    @LogUserAction(actionType = "DELETE_POST")
    @PreAuthorize(
            "hasPermission(#postId,'Post','CAN_DELETE_OWN_POST') or hasAuthority('CAN_DELETE_ANY_POST')"
    )
    public void deletePost(UserPrincipal userPrincipal, Long postId) {
        User user = userPrincipal.getUser();
        Optional<Post> currPost = postRepository.findPostByPostId(postId);
        if(currPost.isEmpty()){
            throw new PostNotFoundException("No such post found");
        }
        Post dbPost = currPost.get();
        if(!dbPost.getUser().getUserId().equals(user.getUserId())){
            throw new RuntimeException("You are not authorized to delete this post");
        }
        postRepository.delete(dbPost);
    }

    public Page<PostDTO> getAllPosts(int page,int size) {
        Pageable pageable = PageRequest.of(page-1,size);
        Page<Post> pagePosts = postRepository.findAll(pageable);
        Page<PostDTO> pagePostDTO = pagePosts.map(post -> postMapper.toDTO(post));
        return pagePostDTO;
    }

    public PostDTO getPostBySlug(String slug){
        Post post = postRepository.findPostBySlug(slug);
        return postMapper.toDTO(post);
    }
}
