package com.blog.blog.service.serviceBean.AuthService;

import com.blog.blog.Exceptions.AuthExceptions.JWTValidationException;
import com.blog.blog.entity.UserEntity.Role;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.service.AuthService.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JWTServiceBean implements JWTService {


    @Value("${security.jwt.secret}")
    private String SECRET_KEY;

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public int extractTokenVersion(String token) {
        return extractClaims(token,claims -> claims.get("tokenVersion",Integer.class));
    }

    public Long extractUserId(String token){
        return extractClaims(token,claims -> claims.get("uid",Long.class));
    }

    public List<String> extractRoles(String token){
        return extractClaims(token,claims -> claims.get("roles",List.class));
    }

    public String extractTokenType(String token){
        return extractClaims(token,claims -> claims.get("tokenType",String.class));
    }

    public boolean validateAccessToken(String token){
        return validateToken(token,"ACCESS");
    }

    public boolean validateRefreshToken(String token,UserDetails userDetails){
        return validateToken(token,"REFRESH");
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimsTFunction){
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token){
        try{
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        }catch (Exception e){
            throw new JWTValidationException(e.getMessage());
        }
    }

    public boolean validateToken(String token,String tokenType) {
        Claims claims = extractAllClaims(token);
        return claims.get("tokenType").equals(tokenType) &&
                claims.getExpiration().after(new Date());
    }

    private boolean isTokenExpired(String token){
        return extractExpirationDate(token).before(new Date());
    }

    private Date extractExpirationDate(String token){
        return extractClaims(token,Claims::getExpiration);
    }

    public String generateAccessToken(User user, Date expirationTime) {
        return generateToken(user, expirationTime,"ACCESS");
    }

    public String generateRefreshToken(User user,Date expirationTime) {
        return generateToken(user,expirationTime,"REFRESH");
    }

    private String generateToken(User user, Date expirationTime, String tokenType){
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("uid",user.getUserId())
                .claim("tokenType",tokenType)
                .claim("roles",user.getRoles().stream().map(Role::getRoleName).toList())
                .claim("tokenVersion",user.getTokenVersion())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expirationTime)
                .signWith(getSigningKey()).compact();
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
