package com.blog.blog.repository.PostRepository;

import com.blog.blog.entity.PostEntity.Bookmark;
import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.entity.UserEntity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {

    @Query("SELECT b.post FROM Bookmark b where b.user=:user")
    Page<Post> findBookmarkedPostByUser(@Param("user") User user, Pageable pageable);
    @Query("SELECT b.post FROM Bookmark b where b.user=:user")
    List<Post> findAllByUser(@Param("user") User user);
    void deleteByUserAndPost(User user, Post post);
}
