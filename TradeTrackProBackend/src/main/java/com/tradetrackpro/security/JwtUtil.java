package com.tradetrackpro.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.security.Key;

@Component
public class JwtUtil {

    // Use a fixed secret key for development (in production, load from properties)
    private final Key secretKey = Keys.hmacShaKeyFor("MySuperSecretKeyForTradeTrackPro1234567890".getBytes());
    private final long expirationMs = 24 * 60 * 60 * 1000; // 24 hours

    public String generateToken(Long userId, String username) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    public String getUsername(String token) {
        return extractClaims(token).get("username", String.class);
    }
}
