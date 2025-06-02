package kr.co.apiserver.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long REFRESH_TOKEN_TIME= 7 * 24 * 60 * 60 * 1000L;

    // 리프레시 토큰 저장
    public void saveRefreshToken(String username, String refreshToken) {
        redisTemplate.opsForValue().set(
        "RT:" + username,   // key
            refreshToken,       // value
            REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS
        );
    }

    // 리프레시 토큰 조회
    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get("RT:" + username);
    }

    // 리프레시 토큰 삭제
    public void deleteRefreshToken(String username) {
        redisTemplate.delete("RT:" + username);
    }

}
