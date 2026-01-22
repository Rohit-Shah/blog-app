package com.blog.blog.repository.UserRepository;

import com.blog.blog.entity.UserEntity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {
}
