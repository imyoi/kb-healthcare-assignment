package com.kb.healthcare.myohui.global.jwt;

import com.kb.healthcare.myohui.global.enums.ErrorCode;
import com.kb.healthcare.myohui.global.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @Value("${jwt.access-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private final RedisService redisService;

    @PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long memberId, String email) {
        return createToken(memberId, email, accessTokenExpiration);
    }

    public String createRefreshToken(Long memberId, String email) {
        String token = createToken(memberId, email, refreshTokenExpiration);
        redisService.setRefreshToken(memberId, token, refreshTokenExpiration / 1000);
        return token;
    }

    /**
     * JWT 토큰 생성
     */
    private String createToken(Long memberId, String email, long expirationTime) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token expired: {}", e.getMessage());
            throw new CustomException(ErrorCode.AUTH_EXPIRED_TOKEN);
        } catch (JwtException e) {
            log.warn("Invalid JWT Token: {}", e.getMessage());
            throw new CustomException(ErrorCode.AUTH_INVALID_TOKEN);
        } catch (Exception e) {
            log.error("Unexpected JWT validation error: {}", e.getMessage());
            throw new CustomException(ErrorCode.AUTH_INVALID_TOKEN);
        }
    }

    /**
     * 토큰에서 회원 ID 추출
     */
    public Long getMemberIdFromToken(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    /**
     * 토큰에서 이메일 추출
     */
    public String getEmailFromToken(String token) {
        return parseClaims(token).get("email", String.class);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}