package com.blog.blog.serviceBean.AuthService;

import com.blog.blog.DTO.UserRequest.UserDTO;
import com.blog.blog.DTO.UserRequest.UserRegistrationRequest;
import com.blog.blog.Exceptions.UserAlreadyExistsException;
import com.blog.blog.constants.UserConstants.UserProfileStatus;
import com.blog.blog.constants.UserConstants.UserRoles;
import com.blog.blog.entity.UserEntity.Role;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.mapper.UserMapper;
import com.blog.blog.repository.UserRepository.RoleRepository;
import com.blog.blog.repository.UserRepository.UserRepository;
import com.blog.blog.service.AuthService.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class RegistrationServiceBean implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public UserDTO register(UserRegistrationRequest registerUserReq) {
        // Check if user already exists
        String username = registerUserReq.getUsername();
        String email = registerUserReq.getEmail();

        if (userRepository.findUserByUsername(username) != null) {
            throw new UserAlreadyExistsException("A user with this username already exists");
        }
        if (userRepository.findUserByEmail(email) != null) {
            throw new UserAlreadyExistsException("A user with this email already exists");
        }

        User newUser = userMapper.toEntity(registerUserReq);

        setNewUserDefaults(newUser);
        // Hash the password before saving
        newUser.setPassword(passwordEncoder.encode(registerUserReq.getPassword()));

        // Save user to DB
        try{
            User savedUser = userRepository.save(newUser);
            return userMapper.toDTO(savedUser);
        }catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("Data violated while saving user");
        }
    }

    private void setNewUserDefaults(User newUser) {
        Role userRole = roleRepository.findByRoleName(UserRoles.USER.toString()).orElseThrow(() -> new RuntimeException("Role not found"));
        newUser.setRoles(Set.of(userRole));
        newUser.setDeleted(false);
        newUser.setStatus(UserProfileStatus.ACTIVE);
    }
}
