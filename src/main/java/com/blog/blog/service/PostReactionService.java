package com.blog.blog.service;

import com.blog.blog.DTO.PostDTO;
import com.blog.blog.DTO.PostReactionDTO;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.entity.Post;
import com.blog.blog.entity.PostReaction;
import com.blog.blog.entity.User;
import com.blog.blog.entity.UserPrincipal;
import com.blog.blog.repository.PostReactionRepository;
import com.blog.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostReactionService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostReactionRepository postReactionRespository;

    public PostReactionDTO reactToPost(UserPrincipal userPrincipal, Long postId, String reactionTypeString) {
        //who is the user
        User user = userPrincipal.getUser();
        //invalid user
        if(user == null){
            throw new UsernameNotFoundException("No such user exists or user is not logged in");
        }
        //on which post the user wants to react
        Optional<Post> currPost = postRepository.findPostByPostId(postId);
        //does that post exists
        if(currPost.isEmpty()){
            throw new PostNotFoundException("No such post exists or the post has been deleted");
        }
        Post post = currPost.get();
        PostReaction.ReactionType reactionTypePost = PostReaction.ReactionType.valueOf(reactionTypeString.toUpperCase());
        //does this user have reacted to this post
        Optional<PostReaction> existingPostReaction = postReactionRespository.findByUserAndPost(user,post);
        //no prev reactions
        PostReactionDTO postReactionDTO = new PostReactionDTO();

        if(existingPostReaction.isEmpty()){
            PostReaction newPostReaction = new PostReaction();
            newPostReaction.setPost(post);
            newPostReaction.setUser(user);
            newPostReaction.setReactionType(reactionTypePost);
            postReactionDTO.setReactionType(reactionTypeString);
            postReactionRespository.save(newPostReaction);
        }
        else{
            //already reacted to this post
            //check if reaction type is same as prev reaction type
            PostReaction postReaction = existingPostReaction.get();
            PostReaction.ReactionType prevReactionType = postReaction.getReactionType();
            //clicking the same reaction again
            if(prevReactionType.equals(reactionTypePost)){
                postReactionRespository.delete(postReaction);
                postReactionDTO.setReactionType("");
            }
            else{
                //changing the type of reaction
                //set the kind of reaction and save it
                postReaction.setReactionType(reactionTypePost);
                postReactionRespository.save(postReaction);
                postReactionDTO.setReactionType(reactionTypeString);
            }
        }
        long postLikeCount = postReactionRespository.countPostLikes(postId);
        long postDislikeCount = postReactionRespository.countPostDislikes(postId);
        postReactionDTO.setLikeCount(postLikeCount);
        postReactionDTO.setDislikeCount(postDislikeCount);
        return postReactionDTO;
    }
}
