package kr.co.apiserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.apiserver.dto.IdempotencyInfo;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long REFRESH_TOKEN_TIME= 7 * 24 * 60 * 60 * 1000L;
    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    /* Refresh Token */
    // 리프레시 토큰 저장
    public void saveRefreshToken(String userName, String refreshToken) {
        redisTemplate.opsForValue().set(
        REFRESH_TOKEN_PREFIX + userName,   // key
            refreshToken,       // value
            REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS
        );
    }

    // 리프레시 토큰 조회
    public String getRefreshToken(String userName) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userName);
    }

    // 리프레시 토큰 삭제
    public void deleteRefreshToken(String userName) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userName);
    }

}
