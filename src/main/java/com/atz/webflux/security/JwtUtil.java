package com.atz.webflux.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    public final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000; // 5 hours
    @Value("${jjwt.secret}")
    private String secret;

    public String generateToken(User user){
        //Cargar los Claims/Payload
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("username", user.getUsername());
        claims.put("test-claim", "test-value");

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .claims(claims) //Payload
                .subject(user.getUsername()) //Para quien va dirigido el token
                .issuedAt(new Date(System.currentTimeMillis())) //Fecha de creacion
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) //Fecha de expiracion
                .signWith(key) //Algoritmo de firma y la clave secreta
                .compact();
    }

    public Claims getAllClaimsFromToken(String token){
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String getUsernameFromToken(String token){
        return getAllClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token){
        return getAllClaimsFromToken(token).getExpiration();
    }

    public boolean validateToken(String token){
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
