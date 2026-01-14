package com.blog.blog.repository.PostRepository;

import com.blog.blog.entity.PostEntity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
    Optional<Post> findById(Long postId);

    Optional<Post> findPostByPostId(Long postId);
    List<Post> findPostByUserId(Long authorId);

    @Query("select p from Post p where p.userId = :userId")
    Page<Post> findAllUserPosts(Pageable pageable,long userId);

    @Query("select p from Post p ORDER BY p.createdAt DESC")
    Page<Post> findPostOrderByCreatedAtDesc(Pageable pageable);
    @Query("select p from Post p ORDER BY p.likeCount DESC")
    Page<Post> findPopularPosts(Pageable pageable);

    @Query("""
            SELECT p from Post p WHERE p.userId IN :userIds
            AND p.createdAt >= :cutoff
            ORDER BY p.createdAt DESC
            """)
    Page<Post> findRecentPostsForUsers(@Param("userIds") List<Long> userIds, @Param("cutoff") Instant cutoff, Pageable pageable);

    @Modifying
    @Query("""
            UPDATE Post p SET p.viewCount = :viewCount where p.postId = :postId
            """)
    void updateViewCountForPost(Long postId, Long viewCount);
}
