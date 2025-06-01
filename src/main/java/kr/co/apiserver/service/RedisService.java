package kr.co.apiserver.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long REFRESH_TOKEN_TIME= 7 * 24 * 60 * 60 * 1000L;

    // 저장
    public void saveRefreshToken(String username, String refreshToken) {


            try {
                redisTemplate.opsForValue().set(
                        "RT:" + username,   // key
                        refreshToken,       // value
                        REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS
                );


                String test = redisTemplate.opsForValue().get("RT:" + username);
                System.out.println("✅ Redis 연결 성공, value = " + test);
            } catch (Exception e) {
                System.err.println("❌ Redis 연결 실패: " + e.getMessage());
                e.printStackTrace();
            }




    }

    // 조회
    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get("RT:" + username);
    }

    // 삭제
    public void deleteRefreshToken(String username) {
        redisTemplate.delete("RT:" + username);
    }

}
