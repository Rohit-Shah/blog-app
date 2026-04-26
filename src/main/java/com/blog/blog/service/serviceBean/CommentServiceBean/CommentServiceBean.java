package com.blog.blog.service.serviceBean.CommentServiceBean;

import com.blog.blog.DTO.CommentReqeust.CommentDTO;
import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.Exceptions.PostExceptions.PostNotFoundException;
import com.blog.blog.entity.CommentEntity.Comment;
import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.mapper.CommentMapper.CommentMapper;
import com.blog.blog.mapper.PostMapper.PostMapper;
import com.blog.blog.repository.CommentRepository.CommentRepository;
import com.blog.blog.repository.CommentRepository.CommentSpecification;
import com.blog.blog.service.CommentService.CommentService;
import com.blog.blog.service.PostService.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CommentServiceBean implements CommentService {

    private final PostService postService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public CommentDTO addCommentOnPost(UserPrincipal userPrincipal, CommentDTO commentRequest, Long postId) {
        User user = userPrincipal.getUser();
        PostDTO postDTO = postService.getPostById(postId);
        Post post = postMapper.toEntity(postDTO);
        if(postDTO == null){
            throw new PostNotFoundException("No such post found");
        }
        Comment comment = commentMapper.toEntity(commentRequest);
        comment.setUser(user);
        comment.setPost(post);
        return commentMapper.toDTO(commentRepository.save(comment));
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasPermission(#commentId,'Comment','CAN_EDIT_OWN_COMMENT') or hasAuthority('CAN_EDIT_ANY_COMMENT')"
    )
    public CommentDTO updateCommentOnPost(UserPrincipal userPrincipal, CommentDTO commentRequest, Long postId) {
        PostDTO postDTO = postService.getPostById(postId);
        if(postDTO == null){
            throw new PostNotFoundException("No such post found");
        }
        Comment comment = commentMapper.toEntity(commentRequest);
        if(commentRequest.getContent() != null && !commentRequest.getContent().isBlank()){
            comment.setContent(commentRequest.getContent());
        }
        return commentMapper.toDTO(commentRepository.save(comment));
    }

    @Override
    @Transactional
    @PreAuthorize(
            "hasPermission(#commentId,'Comment','CAN_DELETE_OWN_COMMENT') or hasAuthority('CAN_DELETE_ANY_COMMENT')"
    )
    public void deleteCommentOnPost(UserPrincipal userPrincipal, Long postId, Long commentId) {
        PostDTO postDTO = postService.getPostById(postId);
        if(postDTO == null){
            throw new PostNotFoundException("No such post found");
        }
        commentRepository.deleteComment(commentId);
    }

    public Page<CommentDTO> getPostComments(int page,int size,Long postId) {
        Specification<Comment> spec = Specification.where(CommentSpecification.isTopLevel());
        Pageable commentPageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> pageComments = commentRepository.findAll(spec, commentPageRequest);
        Page<CommentDTO> pageCommentsDTO = pageComments.map(comment -> commentMapper.toDTO(comment));
        return pageCommentsDTO;
    }
    public Page<CommentDTO> getCommentReplies(int page,int size,Long parentCommentId){
        //Specification
        Specification<Comment> spec = Specification.where(CommentSpecification.belongsToParentCommentId(parentCommentId));
        Pageable pageable = PageRequest.of(page-1,size,Sort.by(Sort.Direction.DESC,"createdAt"));
        Page<Comment> commentReplies = commentRepository.findAll(spec,pageable);
        Page<CommentDTO> commentDTOS = commentReplies.map(comment -> commentMapper.toDTO(comment));
        return commentDTOS;
    }

    @Override
    public CommentDTO getUserCommentByCommentId(Long userId, Long commentId) {
        return commentMapper.toDTO(commentRepository.findUserCommentByCommentId(userId,commentId));
    }

}
