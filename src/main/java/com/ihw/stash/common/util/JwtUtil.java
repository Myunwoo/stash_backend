package com.ihw.stash.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "your_secret_key";
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 5; // 5분
    private final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7일

    // 액세스 토큰 생성
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // JWT 토큰에서 사용자 이름 추출
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token, String username) {
        return extractUsername(token) != null && extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    // 만료 시간 확인 메서드 추가
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;  // 만료된 토큰은 만료로 간주
        } catch (Exception e) {
            return true;  // 기타 예외 시 만료로 간주
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(token); // 토큰이 만료되지 않은 경우에만 유효
        } catch (Exception e) {
            return false; // 예외 발생 시 토큰이 유효하지 않음
        }
    }

    // 모든 Claims 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

}
