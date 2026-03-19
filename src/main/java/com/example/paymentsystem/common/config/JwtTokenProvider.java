package com.example.paymentsystem.common.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidity = 1000L * 60 * 15; // 15분
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 14;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        // String으로 넘어온 secret을 byte 배열로 디코딩 후 SecretKey 객체로 변환
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }
    //토큰 생성
    public String createAccessToken(Long id) {
        return createToken(id, accessTokenValidity);
    }

    // 2. 리프레시 토큰 생성 (등급 정보 없이 ID만 담거나 최소 정보만 담음)
    public String createRefreshToken(Long id) {
        return createToken(id, refreshTokenValidity);
    }

    public String createToken(Long id, long validity) {
        Date now = new Date();

        var builder = Jwts.builder()
                .subject(id.toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validity))
                .signWith(secretKey);

            return builder.compact();
    }

    //토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey) // 파서에 키 설정
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //id 추출
    public Long getUserId(String token) {
        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.parseLong(subject);
    }
}
