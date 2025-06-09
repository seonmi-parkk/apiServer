package kr.co.apiserver.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.response.exception.CustomAuthenticationException;
import kr.co.apiserver.response.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // access 토큰 만료시간
    private static final long ACCESS_TOKEN_TIME = 15 * 60 * 1000L; // 15분
    // refresh 토큰 만료시간
    private static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일
    private static final long TWO_DAYS = 2 * 24 * 60 * 60 * 1000L; // 3일


    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(User user) {
        return Jwts.builder()
                .setHeader(Map.of("typ"
                        ,"JWT"))
                .setSubject(user.getEmail())
                .claim(AUTHORIZATION_KEY, user.getUserRoleList())
                .claim("nickname", user.getNickname())
                .claim("isSocial", user.isSocial())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String createRefreshToken(String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public Map<String, String> refreshAccessToken(String refreshToken, User user) {
        // Access 토큰 재발급
        String newAccessToken = createAccessToken(user);

        // Refresh Token의 유효기간이 2일 이하인 경우 재뱔급
        String newRefreshToken = isRefreshTokenAboutToExpire(refreshToken) ? createRefreshToken(user.getEmail()) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    public String validateToken(String token) {
        String username = null;
        try{
            username = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                    .getBody()
                    .getSubject();
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.info("INVALID_TOKEN");
            throw new CustomAuthenticationException(ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info("EXPIRED_TOKEN");
            throw new CustomAuthenticationException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token");
            throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty");
            throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED);
        } catch (Exception e) {
            log.info("JWT token processing error");
            throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED);
        }

        return username;
    }

    public Boolean isExpired(String token){
        log.info("isExpired : " );
        Date expDate = Jwts.claims().getExpiration();
        // 현재 날짜가 exp 날짜보다 뒤에 있으면, 만료됨
        return new Date().after(expDate);
    }

    public boolean isRefreshTokenAboutToExpire(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        long now = System.currentTimeMillis();
        long tokenExpiry = claimsJws.getBody().getExpiration().getTime();

        // 만료까지 남은 시간이 2일 이하인 경우
        return (tokenExpiry - now) <= TWO_DAYS;
    }

    public Claims parseClaims(String token) {
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

    public String parseUserName(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new CustomAuthenticationException(ErrorCode.EXPIRED_TOKEN);
        }
    }

}
