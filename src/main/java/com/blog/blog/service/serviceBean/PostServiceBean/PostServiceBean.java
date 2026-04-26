package com.blog.blog.service.serviceBean.PostServiceBean;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.DTO.PostRequest.PostSearchCriteria;
import com.blog.blog.Exceptions.AuthExceptions.UnauthorizedException;
import com.blog.blog.Exceptions.PostExceptions.PostNotFoundException;
import com.blog.blog.Response.CursorResponse;
import com.blog.blog.Response.PageResponse;
import com.blog.blog.annotations.LogUserAction;
import com.blog.blog.config.AppProperties;
import com.blog.blog.constants.PostContants.PostStatus;
import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.mapper.PostMapper.PostMapper;
import com.blog.blog.repository.Cursor.Cursor;
import com.blog.blog.repository.Cursor.CursorCodec;
import com.blog.blog.repository.PostRepository.PostRepository;
import com.blog.blog.repository.PostRepository.PostSpecification;
import com.blog.blog.repository.UserRepository.UserRepository;
import com.blog.blog.service.PostService.PostService;
import com.blog.blog.service.cache.keys.PostCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceBean implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostCacheService postCacheService;

    @Override
    @LogUserAction(actionType = "CREATE_POST")
    @Transactional
    public PostDTO addPost(UserPrincipal userPrincipal, PostDTO postRequest, String imageUrl) {
        User user = userPrincipal.getUser();
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
        Post dbPost = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("No such post found"));
        if(!Objects.equals(dbPost.getUser().getUserId(), userId)){
            throw new UnauthorizedException("You are not authorized to update this post");
        }
        return dbPost;

    }

    private String getPostSlug(String postTitle){
        String base = postTitle.toLowerCase().trim().replaceAll("[^a-z0-9\\\\s]","").replaceAll("\\s","-");
        String slug = base;
        int counter = 1;
        while(postRepository.existsBySlug(slug)){
            slug = base + "-" + counter;
            counter++;
        }
        return slug;
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
    public PostDTO getUserPostById(Long userId, Long postId) {
        Post post = postRepository.findUserPostByPostId(userId,postId).orElseThrow(() -> new PostNotFoundException("No such post found"));
        return postMapper.toDTO(post);
    }

    @Override
    @LogUserAction(actionType = "UPDATE_POST")
    @PreAuthorize(
            "hasPermission(#postId,'Post','CAN_EDIT_OWN_POST') or hasAuthority('CAN_EDIT_ANY_POST')"
    )
    @Transactional
    public PostDTO updatePost(UserPrincipal userPrincipal, Long postId, PostDTO updatedPost, String imageUrl)  {

        Post dbPost = postRepository.findPostByPostId(postId)
                .orElseThrow(() -> new PostNotFoundException("No such post found"));

        if (updatedPost.getTitle() != null && !updatedPost.getTitle().isBlank()) {
            dbPost.setTitle(updatedPost.getTitle());
        }

        if (updatedPost.getContent() != null && !updatedPost.getContent().isBlank()) {
            dbPost.setContent(updatedPost.getContent());
        }
        if(updatedPost.getCategory() != null){
            dbPost.setCategory(updatedPost.getCategory());
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
    @Transactional
    public void deletePost(UserPrincipal userPrincipal, Long postId) {
        postRepository.deletePost(postId);
    }

    @Override
    @LogUserAction(actionType = "GET_ALL_POST")
    public CursorResponse<PostDTO> getAllPosts(int size, PostSearchCriteria criteria) {
        Specification<Post> spec = buildPostSpecification(criteria);
        Pageable pageable = PageRequest.of(0,size+1, Sort.by(Sort.Order.desc("createdAt"),Sort.Order.desc("postId")));
        //cache response
        Optional<CursorResponse<PostDTO>> cachedPostResponse = postCacheService.getCachedPostResponse(size,criteria);
        if(cachedPostResponse.isPresent()){
            log.info("Returning cached data for post");
            return cachedPostResponse.get();
        }
        Page<Post> pagePosts = postRepository.findAll(spec,pageable);
        List<Post> postContent = pagePosts.getContent();
        boolean hasNext = postContent.size() > size;
        if(hasNext){
            postContent = postContent.subList(0,size);
        }
        String nextCursor = null;
        if(!postContent.isEmpty() && hasNext){
            Post lastPost = postContent.get(postContent.size()-1);
            Cursor newCursor = new Cursor(lastPost.getCreatedAt(),lastPost.getPostId());
            try {
                nextCursor = CursorCodec.encodeCursor(newCursor);
            } catch (JsonProcessingException e) {
                log.error("Error while processing json {} ",e.getMessage());
                throw new RuntimeException(e);
            }
        }
        List<PostDTO> postDTOList = postContent.stream().map(postMapper::toDTO).toList();
        CursorResponse<PostDTO> postDTOCursorResponse = CursorResponse.from(postDTOList,nextCursor,hasNext,postDTOList.size());
        postCacheService.cachePostResponse(size,criteria,postDTOCursorResponse);
        return postDTOCursorResponse;
    }

    @Override
    public PostDTO getPostBySlug(String slug){
        Post post = postRepository.findPostBySlug(slug).orElseThrow(() -> new PostNotFoundException("No such post found"));
        return postMapper.toDTO(post);
    }

    private Specification<Post> buildPostSpecification(PostSearchCriteria criteria) {
        Specification<Post> spec = Specification.where(PostSpecification.isNotDeleted());
        if(criteria.getCursor() != null){
            try{
                Cursor currentCursor = CursorCodec.decodeCursor(criteria.getCursor());
                spec = spec.and(PostSpecification.afterCursor(currentCursor));
            }catch (JsonProcessingException e){
                log.error("Error while processing cursor {}",e.getMessage());
                throw new RuntimeException("Invalid pagination cursor");
            }
        }
        if(criteria.getUserId() != null){
            spec = spec.and(PostSpecification.getUserSpecificPost(criteria.getUserId()));
        }
        if(criteria.getContent() != null &&  !criteria.getContent().isEmpty()){
            spec = spec.and(PostSpecification.getKeyWordSpecificPost(criteria.getContent()));
        }
        if(criteria.getCategory() != null){
            spec = spec.and(PostSpecification.getCategorySpecificPost(criteria.getCategory()));
        }
        return spec;
    }
}
