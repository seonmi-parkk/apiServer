package kr.co.apiserver.controller;

import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/user")
public class UserController {

    private final JwtUtil jwtUtil;

    @PostMapping("/refresh")
    public Map<String, Object> refresh(
            @RequestHeader("Authorization") String authHeader,
            String refreshToken
    ) {
        log.info("refresh 요청 ");
        if (refreshToken == null) {
            log.error("refresh token is null");
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (authHeader == null || authHeader.length() < 7) {
            log.error("access token is null or invalid format");
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String accessToken = authHeader.substring(7);
        log.info("accessToken : " + accessToken);

        // Refresh 토큰 검증
        jwtUtil.validateToken(refreshToken);

        log.info(" Refresh 토큰 검증");
        // claims 가져오기
        Map<String, Object> claims = jwtUtil.parseClaims(accessToken);

        // Access 토큰 재발급
        String newAccessToken = jwtUtil.createAccessToken(claims);

        // Refresh Token의 유효기간이 2일 이하인 경우 재뱔급
        String newRefreshToken = jwtUtil.isRefreshTokenAboutToExpire(refreshToken) ? jwtUtil.createRefreshToken() : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);

    }

}
