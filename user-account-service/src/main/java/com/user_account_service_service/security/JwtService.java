package com.user_account_service_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    private SecretKey key;

    // khoi tao secret key
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }


    // build chuoi jwt
    public String generateToken(String username , List<String> roles){
        return Jwts.builder()
                .subject(username)
                .claim("roles",roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key,Jwts.SIG.HS512)
                .compact();
    }

    public String extractUsername(String token){
        return extractClaim(token,
                    c->c.getSubject()
                );
    }

    public boolean isTokenValid(String token , String username){
        final String name = extractUsername(token);
        return (name.equals(username) && !isTokenExpired(token));
    }

    //phan tich jwt lay ra phan payload
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // lay ra username , expiration,role o token
    private <T> T extractClaim(String token , Function<Claims,T> claimsTFunction){
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    //kiem tra token het han
    private boolean isTokenExpired(String token){
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }





}
