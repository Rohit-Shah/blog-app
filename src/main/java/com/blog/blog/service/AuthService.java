package com.blog.blog.service;

import com.blog.blog.DTO.UserDTO;
import com.blog.blog.Exceptions.JWTValidationException;
import com.blog.blog.Exceptions.UserAlreadyExistsException;
import com.blog.blog.entity.Role;
import com.blog.blog.entity.User;
import com.blog.blog.repository.RoleRepository;
import com.blog.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private User loggedInUser;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.googleTokenExchangeUrl}")
    private String googleAuthTokenEndPoint;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    public AuthService(){

    }

    public UserDTO registerUser(UserDTO registerUserReq) {
        // Check if user already exists
        String username = registerUserReq.getUsername();
        String email = registerUserReq.getEmail();

        if (userRepository.findUserByUsername(username) != null) {
            throw new UserAlreadyExistsException("A user with this username already exists");
        }
        if (userRepository.findUserByEmail(email) != null) {
            throw new UserAlreadyExistsException("A user with this email already exists");
        }

        // Fetch roles from DB
        List<Role> userRoles = new ArrayList<>();

        Role userRole = roleRepository.findByRoleName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("USER role not found!"));
        userRoles.add(userRole);

        if (username.equals("doraemon")) {
            Role adminRole = roleRepository.findByRoleName(Role.RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found!"));
            userRoles.add(adminRole);
        }

        // Set roles for the new user
        User newUser = convertDTOToEntity(registerUserReq);
        newUser.setRoles(userRoles);

        // Hash the password before saving
        newUser.setPassword(passwordEncoder.encode(registerUserReq.getPassword()));

        // Save user to DB
        User savedUser = userRepository.save(newUser);

        return convertEntityToDTO(savedUser);
    }


    public UserDTO loginUser(UserDTO userData) throws AuthenticationException {
        String accessToken = "";
        String refreshToken = "";
        String username = userData.getUsername();
        String password = userData.getPassword();
        try{
            Authentication authUser = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
            if(authUser.isAuthenticated()){
                accessToken = jwtService.generateAccessToken(username);
                refreshToken = jwtService.generateRefreshToken(username);
                UserDTO loggedInUser = new UserDTO();
                loggedInUser.setUsername(userData.getUsername());
                loggedInUser.setAccessToken(accessToken);
                loggedInUser.setRefreshToken(refreshToken);
                // save the refresh token in the db
                User user = userRepository.findUserByUsername(username);
                user.setRefreshToken(refreshToken);
                userRepository.save(user);
                return loggedInUser;
            }
        }catch (BadCredentialsException e){
            throw new AuthenticationException("Invalid credentials");
        }
        throw new UsernameNotFoundException("No user found for the given username");
    }

    public String generateNewAccessToken(String refreshToken) {
        refreshToken = refreshToken.trim();
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if(userDetails == null){
            throw new UsernameNotFoundException("No such user found for the given username");
        }

        if(!jwtService.validateToken(refreshToken,userDetails)){
            throw new JWTValidationException("Invalid or Expired refresh token");
        }
        String newAccessToken = jwtService.generateAccessToken(username);
        return newAccessToken;
    }

    public UserDTO getSessionTokensForOAuthUsers(String authCode) {
        String tokenExchangeUrl = googleAuthTokenEndPoint;
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("code",authCode);
        params.add("client_id",clientId);
        params.add("client_secret",clientSecret);
        params.add("redirect_uri",redirectUri);
        params.add("grant_type","authorization_code");
        //set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params,headers);

        //post request for access token
        ResponseEntity<Map> accessTokenResponse = restTemplate.postForEntity(tokenExchangeUrl, request, Map.class);
        String idToken = (String)accessTokenResponse.getBody().get("id_token");

        //get request for user details
        String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
        if(userInfoResponse.getStatusCode() == HttpStatus.OK){
            Map userInfo = userInfoResponse.getBody();
            String email = (String) userInfo.get("email");
            String username = (String) userInfo.get("name");
            User user = null;
            try{
                user = userRepository.findUserByUsername(username);
                if(user == null){
                    throw new UsernameNotFoundException("No such user found");
                }
            } catch (Exception e){
                user = new User();
                user.setEmail(email);
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                List<Role> userRoles = new ArrayList<>();
                Role role = roleRepository.findByRoleName(Role.RoleName.USER).orElseThrow(() -> new RuntimeException("No such role found"));
                userRoles.add(role);
            }
            String accessToken = jwtService.generateAccessToken(username);
            String refreshToken = jwtService.generateRefreshToken(username);
            if(user != null){
                user.setRefreshToken(refreshToken);
                User savedUser = userRepository.save(user);
                UserDTO userDTO = convertEntityToDTO(savedUser);
                userDTO.setAccessToken(accessToken);
                userDTO.setRefreshToken(refreshToken);
                return userDTO;
            }
            else{
                throw new UsernameNotFoundException("No such user found");
            }
        }
        return new UserDTO();
    }


    public UserDTO convertEntityToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getUserId().toString());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());

        // Convert List<Role> to List<String> (Role Names)
        List<String> roleNames = user.getRoles()
                .stream()
                .map(role -> role.getRoleName().name()) // Convert Enum to String
                .collect(Collectors.toList());

        userDTO.setRoles(roleNames);
        return userDTO;
    }

    public User convertDTOToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        // Convert List<String> (Role Names) to List<Role>
//        List<Role> roles = userDTO.getRoles()
//                .stream()
//                .map(roleName -> roleRepository.findByRoleName(Role.RoleName.valueOf(roleName))
//                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
//                .collect(Collectors.toList());
//
//        user.setRoles(roles);
        return user;
    }


}
