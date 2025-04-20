package com.blog.blog.service;

import com.blog.blog.DTO.CommentDTO;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.entity.Comment;
import com.blog.blog.entity.Post;
import com.blog.blog.entity.User;
import com.blog.blog.entity.UserPrincipal;
import com.blog.blog.repository.CommentRepository;
import com.blog.blog.repository.PostRepository;
import com.blog.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

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
        Optional<Post> existingPost = postRepository.findPostByPostId(postId);
        if(existingPost.isEmpty()){
            throw new PostNotFoundException("Not such post exists");
        }
        Post post = existingPost.get();
        List<Comment> allPostCommentsList = commentRepository.findCommentsByPostId(postId);
        List<CommentDTO> allPostCommentsDTOlist = allPostCommentsList.stream().map(comment -> convertCommentEntityToDTO(comment, user, post)).toList();
        return allPostCommentsDTOlist;
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
