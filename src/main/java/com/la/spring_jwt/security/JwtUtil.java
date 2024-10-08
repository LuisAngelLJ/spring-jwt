package com.la.spring_jwt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private final static String secretKey = "eSfs9PdkDps4rH8wN2k5fZv1T2k8aT9iT4sJ5aP0nH3uT0vS1uM6oB1tI2vN4kU8L6tS5pW2xJ3oK9zL8bO9wR7cV1mT7aF2eQ3";

    private final static Long expirationTime = 86400000L;

    public String generateToken(UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", customUserDetails.getEmail());
        claims.put("id", customUserDetails.getId());
        claims.put("roles", customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList())); // Agregar roles a los claims

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customUserDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        if (token == null) return null;
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            return claims;
        } catch (Exception e) {
            System.out.println("Error al decodificar el token " + e.getMessage());
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractClaims(token).getSubject();
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
}
