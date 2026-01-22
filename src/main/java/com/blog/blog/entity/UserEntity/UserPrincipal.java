package com.blog.blog.entity.UserEntity;

import com.blog.blog.service.AuthService.LoginAttemptService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class UserPrincipal implements UserDetails {

    private User user;
    private LoginAttemptService loginAttemptService;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public UserPrincipal(User user, LoginAttemptService loginAttemptService){
        this.user = user;
        this.loginAttemptService = loginAttemptService;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //roles
        Stream<GrantedAuthority> roleAuthorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRoleName().toString()));
        //permissions
        Stream<GrantedAuthority> permissionAuthorities = user.getRoles().stream().flatMap(role -> role.getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.getPermissionName())));

        return Stream.concat(roleAuthorities,permissionAuthorities).distinct().toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.loginAttemptService.isBlocked(user.getUsername());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

}
