package com.ahmed.E_CommerceApp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    public String secret;
    @Value("${jwt.expiration}")
    public Long expiration;

    public String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, userDetails.getUsername());
    }
    public Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    public <T> T extractClaim(String token , Function<Claims , T> c){
        Claims claims=extractAllClaims(token);
        return c.apply(claims);
    }
    public String extractUserName(String token){
        return extractClaim(token,  Claims::getSubject);
     }
    public Date extractExpiration(String token){
        return extractClaim(token,  Claims::getExpiration);
    }
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public boolean validateToken(String token , UserDetails userDetails){
    String userName=extractUserName(token);
    return (userName.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }

}
