package com.blog.blog.repository;

import com.blog.blog.entity.Post;
import com.blog.blog.entity.PostReaction;
import com.blog.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    Optional<PostReaction> findByUserAndPost(User user, Post post);

    @Query("SELECT COUNT(pr) FROM PostReaction pr WHERE pr.post.postId = :postId AND pr.reactionType = 'LIKE'")
    long countPostLikes(@Param("postId") Long postId);

    @Query("SELECT COUNT(pr) FROM PostReaction pr WHERE pr.post.postId = :postId AND pr.reactionType = 'DISLIKE'")
    long countPostDislikes(@Param("postId") Long postId);

    @Query("SELECT pr.reactionType FROM PostReaction pr WHERE pr.post.postId = :postId and pr.user.userId = :userId")
    Optional<PostReaction.ReactionType> findUserReaction(@Param("userId") Long userId,@Param("postId") Long postId);
}
