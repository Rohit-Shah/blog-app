package com.blog.blog.repository.UserRepository;

import com.blog.blog.entity.UserEntity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u from User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions where u.username=:username")
    User findUserByUsername(@Param("username") String username);
    User findUserByEmail(String email);
    User findUserByUserId(Long userId);
}
