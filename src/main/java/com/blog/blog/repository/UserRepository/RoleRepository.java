package com.blog.blog.repository.UserRepository;

import com.blog.blog.entity.UserEntity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(Role.RoleName roleName);
}
