package kr.co.apiserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    private static final int EXPIRE_MINUTES = 5;

    public String sendCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));

        // Redis에 저장
        redisTemplate.opsForValue().set("EMAIL_CODE:" + email, code, Duration.ofMinutes(EXPIRE_MINUTES));

        // 이메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[Bit Gallery] 이메일 인증 코드");
        message.setText("아래 인증 코드를 입력해주세요:\n\n" + code);
        mailSender.send(message);

        return code;
    }

    public boolean verifyCode(String email, String inputCode) {
        String key = "EMAIL_CODE:" + email;
        String savedCode = redisTemplate.opsForValue().get(key);

        return inputCode.equals(savedCode);
    }

    public void clearCode(String email) {
        redisTemplate.delete("EMAIL_CODE:" + email);
    }

    public void saveVerifiedEmail(String email) {
        redisTemplate.opsForValue().set("VERIFIED_EMAIL:" + email, "true", Duration.ofMinutes(30));
    }

    public boolean isVerified(String email) {
        String key = "VERIFIED_EMAIL:" + email;
        return redisTemplate.hasKey(key);
    }

    public void clearVerifiedEmail(String email) {
        redisTemplate.delete("VERIFIED_EMAIL:" + email);
    }

}
