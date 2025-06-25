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

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static final String IDEMPOTENCY_PREFIX = "IDEMPOTENCY:";

    public static final String LOCK_PREFIX = "LOCK:";
    public static final long LOCK_EXPIRE_TIME = 5000L;

    /**
     * Idempotency Key 생성 메서드
     * @param idempotencyKey - idempotency key
     * @param userEmail - user email
     * @param contextType - 키 종류 (IdempotencyContextType) ex. PAYMENT
     * @return 생성된 idempotency key
     */
    public String makeKey(String idempotencyKey, String userEmail, IdempotencyContextType contextType) {
        return IDEMPOTENCY_PREFIX + contextType + ":" + userEmail + ":" + idempotencyKey;
    }

    /**
     * Idempotency Key 저장 메서드
     * @param key - idempotency key
     * @param userName - user email
     * @param type - 키 종류 (IdempotencyContextType) ex. PAYMENT
     */
    public void saveInitial(String key, String userName, IdempotencyContextType type) {
        IdempotencyInfo info = new IdempotencyInfo(PaymentIdempotencyStatus.READY, null);
        savekey(key, userName, type, info);
    }

    /**
     * redis에 Idempotency Key 저장
     * @param key - idempotency key
     * @param userName - user email
     * @param type - 키 종류 (IdempotencyContextType) ex. PAYMENT
     * @param info - IdempotencyInfo (PaymentIdempotencyStatus, resultUrl)
     */
    public void savekey(String key, String userName, IdempotencyContextType type, IdempotencyInfo info) {
        redisTemplate.opsForValue().set(
                makeKey(key, userName, type),
                serialize(info),
                Duration.ofMinutes(15)
        );
    }

    /**
     * idempotencyKey 검증 및 중복의 경우 기존 응답 반환.
     * @param key -  idempotency key
     * @param userName - user email
     * @param type - 키 종류 (IdempotencyContextType) ex. PAYMENT
     * @return
     * - READY 상태 & 새로운 키: null 반환
     * - 처리 완료 상태: resultUrl 반환
     */
    public String verifyIdempotencyKey(String key, String userName, IdempotencyContextType type) {
        String idempotencyKey = makeKey(key, userName, type);

        // redis에서 idempotencyKey 조회
        String value = redisTemplate.opsForValue().get(idempotencyKey);

        if (value != null) {
            IdempotencyInfo info = deserialize(value);

            // 이미 처리된 요청의 경우 resultUrl 반환
            if (!PaymentIdempotencyStatus.READY.equals(info.getStatus())) {
                return info.getResultUrl();
            }
        }

        return null;
    }

    /**
     * Idempotency Key를 사용된 상태로 변경(used)
     * @param key - idempotency key
     * @param userName - user email
     * @param type - 키 종류 (IdempotencyContextType) ex. PAYMENT
     * @param resultUrl - 결과 URL
     */
    public void markAsUsed(String key, String userName, IdempotencyContextType type, String resultUrl) {
        IdempotencyInfo info = new IdempotencyInfo(PaymentIdempotencyStatus.USED, resultUrl);
        savekey(key, userName, type, info);
    }

    /**
     * Idempotency Key 삭제
     * @param key - idempotency key
     * @param userName - user email
     * @param type - 키 종류 (IdempotencyContextType) ex. PAYMENT
     */
    public void deleteIdempotencyKey(String key, String userName, IdempotencyContextType type) {
        redisTemplate.delete(makeKey(key, userName, type));
    }

    /**
     * Redis Lock을 사용하여 중복 요청 방지
     * @param key - lock 키
     * @return 성공 여부
     */
    public boolean tryLock(String key) {
        String lockKey = LOCK_PREFIX + key;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofMillis(LOCK_EXPIRE_TIME));

        return success != null && success;
    }

    /**
     * Redis Lock 해제
     * @param key - lock 키
     */
    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        redisTemplate.delete(lockKey);
    }

    /**
     * IdempotencyInfo 객체를 JSON 문자열로 직렬화
     * @param info - IdempotencyInfo 객체
     * @return 직렬화된 JSON 문자열
     */
    private String serialize(IdempotencyInfo info) {
        try {
            return objectMapper.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("IdempotencyInfo 직렬화 실패", e);
        }
    }

    /**
     * JSON 문자열을 IdempotencyInfo 객체로 역직렬화
     * @param value - 직렬화된 JSON 문자열
     * @return 역직렬화된 IdempotencyInfo 객체
     */
    private IdempotencyInfo deserialize(String value) {
        try {
            return objectMapper.readValue(value, IdempotencyInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("IdempotencyInfo 역직렬화 실패", e);
        }
    }

}
