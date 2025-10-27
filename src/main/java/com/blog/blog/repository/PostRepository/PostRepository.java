package com.blog.blog.repository.PostRepository;

import com.blog.blog.entity.PostEntity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
    Optional<Post> findById(Long postId);

    Optional<Post> findPostByPostId(Long postId);
    List<Post> findPostByUserId(Long authorId);
}
