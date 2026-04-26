package com.blog.blog.repository.PostRepository;

import com.blog.blog.entity.PostEntity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    Optional<Post> findPostByPostId(Long postId);
    @Query("select p from Post p where p.user.userId = :userId")
    List<Post> findPostByUserId(Long userId);

    @Query("select p from Post p where p.user.userId = :userId")
    Page<Post> findAllUserPosts(Pageable pageable,long userId);

    @Query("select p from Post p ORDER BY p.createdAt DESC")
    Page<Post> findPostOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p from Post p WHERE p.postId = :postId and p.user.userId = :userId")
    Post existsByPostIdAndUserId(Long postId, Long userId);

    Optional<Post> findPostBySlug(String slug);
    @Modifying
    @Query("UPDATE Post p SET p.deleted = true where p.postId = :postId")
    void deletePost(@Param("postId") Long postId);

    @Query("SELECT p from Post p where p.postId = :postId and p.user.userId = :userId")
    Optional<Post> findUserPostByPostId(Long userId, Long postId);

    boolean existsBySlug(String slug);
}
