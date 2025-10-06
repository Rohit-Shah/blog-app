package com.blog.blog.service.PostService;

import com.blog.blog.DTO.PostRequest.PostReactionDTO;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.entity.PostEntity.PostReaction;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.repository.PostRepository.PostReactionRepository;
import com.blog.blog.repository.PostRepository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class PostReactionService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostReactionRepository postReactionRepository;

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
        Post post = currPost.orElseThrow(() -> new PostNotFoundException("No such post exists or the post has been deleted"));
        PostReaction.ReactionType reactionTypePost = PostReaction.ReactionType.valueOf(reactionTypeString.toUpperCase());
        //does this user have reacted to this post
        Optional<PostReaction> existingPostReaction = postReactionRepository.findByUserAndPost(user,post);
        //no prev reactions
        PostReactionDTO postReactionDTO = new PostReactionDTO();

        if(existingPostReaction.isEmpty()){
            PostReaction newPostReaction = new PostReaction();
            newPostReaction.setPost(post);
            newPostReaction.setUser(user);
            newPostReaction.setReactionType(reactionTypePost);
            postReactionDTO.setReactionType(reactionTypeString);
            postReactionRepository.save(newPostReaction);
        }
        else{
            //already reacted to this post
            //check if reaction type is same as prev reaction type
            PostReaction postReaction = existingPostReaction.get();
            PostReaction.ReactionType prevReactionType = postReaction.getReactionType();
            //clicking the same reaction again
            if(prevReactionType.equals(reactionTypePost)){
                postReactionRepository.delete(postReaction);
                postReactionDTO.setReactionType("");
            }
            else{
                //changing the type of reaction
                //set the kind of reaction and save it
                postReaction.setReactionType(reactionTypePost);
                postReactionRepository.save(postReaction);
                postReactionDTO.setReactionType(reactionTypeString);
            }
        }
        long postLikeCount = postReactionRepository.countPostLikes(postId);
        long postDislikeCount = postReactionRepository.countPostDislikes(postId);
        postReactionDTO.setLikeCount(postLikeCount);
        postReactionDTO.setDislikeCount(postDislikeCount);
        return postReactionDTO;
    }
}
