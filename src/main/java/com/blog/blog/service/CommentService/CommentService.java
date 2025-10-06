package com.blog.blog.service.CommentService;

import com.blog.blog.DTO.CommentReqeust.CommentDTO;
import com.blog.blog.Exceptions.CommentNotFoundException;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.entity.CommentEntity.Comment;
import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.repository.CommentRepository.CommentRepository;
import com.blog.blog.repository.PostRepository.PostRepository;
import com.blog.blog.repository.UserRepository.UserRepository;
import com.blog.blog.service.RedisService.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

    public CommentDTO addComment(UserPrincipal userPrincipal, CommentDTO commentDTO,Long postId) {
        User user = userPrincipal.getUser();
        String commentValue = commentDTO.getContent();
        Optional<Post> currPost  = postRepository.findPostByPostId(postId);
        if(currPost.isEmpty()){
            throw new PostNotFoundException("No such post found");
        }
        Post post = currPost.get();
        Comment userComment = convertCommentDTOToEntity(commentDTO,user,post);
        Comment savedComment = commentRepository.save(userComment);
        postRepository.save(post);
        return convertCommentEntityToDTO(savedComment,user,post);
    }

    public List<CommentDTO> getAllPostComments(UserPrincipal userPrincipal, CommentDTO commentDTO,Long postId) {
        User user = userPrincipal.getUser();
        if(user == null){
            throw new UsernameNotFoundException("No user found !! Please login to continue");
        }
        Post existingPost = postRepository.findPostByPostId(postId).orElseThrow(() -> new PostNotFoundException("No such post found"));
        //check if the comments exists in redis
        String postCommentKey = "Comment" + "_" + postId;
        TypeReference<List<CommentDTO>> commentTypeRef = new TypeReference<List<CommentDTO>>(){};
        if(redisService.get(postCommentKey,commentTypeRef) != null){
            return redisService.get(postCommentKey,commentTypeRef);
        }
        else{
            //set the comments in redis
            List<Comment> allPostCommentsList = commentRepository.findCommentsByPostId(postId);
            List<CommentDTO> allPostCommentsDTOlist = allPostCommentsList.stream().map(comment -> convertCommentEntityToDTO(comment, user, existingPost)).toList();
            redisService.set(postCommentKey,allPostCommentsDTOlist,300000l);
            return allPostCommentsDTOlist;
        }
    }

    public CommentDTO deletePostComment(UserPrincipal userPrincipal, Long postId, Long commentId) {
        User user = userPrincipal.getUser();
        if(user == null){
            throw new UsernameNotFoundException("No user found !! Please login to continue");
        }
        Post existingPost = postRepository.findPostByPostId(postId).orElseThrow(() -> new PostNotFoundException("No such post found"));
        Comment postComment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("No such comment found"));
        commentRepository.deleteById(commentId);
        return convertCommentEntityToDTO(postComment,user,existingPost);
    }

    private Comment convertCommentDTOToEntity(CommentDTO commentDTO,User user,Post post){
        Comment comment = new Comment();
        comment.setAuthorId(user.getUserId());
        comment.setContent(commentDTO.getContent());
        comment.setPostId(post.getPostId());
        comment.setAuthorName(user.getUsername());
        return comment;
    }

    private CommentDTO convertCommentEntityToDTO(Comment comment,User user,Post post){
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setAuthorId(user.getUserId().toString());
        commentDTO.setPostId(post.getPostId());
        commentDTO.setContent(comment.getContent());
        Long userId = comment.getAuthorId();
        User dbUser = userRepository.findUserByUserId(userId);
        commentDTO.setAuthorName(dbUser.getUsername());
        return commentDTO;
    }
}
