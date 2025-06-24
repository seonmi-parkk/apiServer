package kr.co.apiserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.apiserver.domain.emums.IdempotencyContextType;
import kr.co.apiserver.domain.emums.PaymentIdempotencyStatus;
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
public class IdempotencyKeyService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String IDEMPOTENCY_PREFIX = "IDEMPOTENCY:";
    private static final String LOCK_PREFIX = "LOCK:";

    private static final long LOCK_EXPIRE_TIME = 5000L;


    // Idempotency Key 저장
    public void saveInitial(String key, String userName, IdempotencyContextType type) {
        IdempotencyInfo info = new IdempotencyInfo(userName, PaymentIdempotencyStatus.READY, type, null);
        try {
            String json = objectMapper.writeValueAsString(info);
            redisTemplate.opsForValue().set(
                IDEMPOTENCY_PREFIX + key,
                json,
                Duration.ofMinutes(15)
            );
        } catch (Exception e) {
            throw new RuntimeException("Idempotency 저장 실패", e);
        }
    }

    // Idempotency Key 검증
    public IdempotencyInfo verifyIdempotencyKey(String key, String userName, IdempotencyContextType type) {
        String json = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + key);

        // Idempotency Key가 존재하지 않는 경우
        if (json == null) {
            throw new CustomException(ErrorCode.INVALID_IDEMPOTENCY_KEY);
        }

        IdempotencyInfo info = deserialize(json);

        // 유저 이름 검증 & key 타입 검증
        if (!info.getUserName().equals(userName) || !info.getType().equals(type)) {
            throw new CustomException(ErrorCode.INVALID_IDEMPOTENCY_KEY);
        }

        // 상태 검증 (이미 사용된 경우)
//            if ("USED".equals(info.getStatus())) {
//                // 이미 처리된 요청의 경우 resultUrl 반환
//                return info.getResultUrl();
//            }

        return info;

    }

    // Idempotency Key 상태 변경(used)
    public void markAsUsed(String key, String userName, IdempotencyContextType type, String resultUrl) {
        IdempotencyInfo info = new IdempotencyInfo(userName, PaymentIdempotencyStatus.USED, type, resultUrl);
        saveInfo(key, info);
    }

    // Idempotency Key 상태 변경(paid)
    public void markAsPaid(String key, String userName, IdempotencyContextType type, String successUrl) {
        IdempotencyInfo info = new IdempotencyInfo(userName, PaymentIdempotencyStatus.PAID, type, successUrl);
        saveInfo(key, info);
    }

    // Idempotency Key 삭제
    public void deleteIdempotencyKey(String key) {
        redisTemplate.delete(IDEMPOTENCY_PREFIX + key);
    }

    // Lock 처리
    public boolean tryLock(String key) {
        String lockKey = LOCK_PREFIX + key;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofMillis(LOCK_EXPIRE_TIME));

        return success != null && success;
    }

    // Lock 해제
    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        redisTemplate.delete(lockKey);
    }

    public void saveInfo(String key, IdempotencyInfo info) {
        redisTemplate.opsForValue().set(
                IDEMPOTENCY_PREFIX + key,
                serialize(info),
                Duration.ofMinutes(15)
        );
    }

    private String serialize(IdempotencyInfo info) {
        try {
            return objectMapper.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("IdempotencyInfo 직렬화 실패", e);
        }
    }

    private IdempotencyInfo deserialize(String value) {
        try {
            return objectMapper.readValue(value, IdempotencyInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("IdempotencyInfo 역직렬화 실패", e);
        }
    }

}
