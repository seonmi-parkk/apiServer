package kr.co.apiserver.controller;

import jakarta.validation.Valid;
import kr.co.apiserver.dto.UserModifyRequestDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.security.UserDetailsImpl;
import kr.co.apiserver.service.RedisService;
import kr.co.apiserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final RedisService redisService;

    // access token 재발급
    @PostMapping("/refresh")
    public ApiResponse<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");
        if (refreshToken == null) {
            log.error("refresh token is null");
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Map<String,String> tokens = userService.refreshAccessToken(refreshToken);

        return ApiResponse.ok(tokens);
    }

    @PostMapping("/auth/kakao")
    public ApiResponse<Map<String,Object>> kakaoCallback(@RequestBody Map<String, String> body) {
        Map<String, Object> result = userService.loginWithKakao(body.get("authCode"));
        return ApiResponse.ok(result);
    }

    @PatchMapping("/modify")
    public ApiResponse<Void> modifyUser(@Valid @RequestBody UserModifyRequestDto requestDto) {
        userService.modifyUser(requestDto);
        return ApiResponse.ok(null);
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("UserController - logout: " + userDetails.getUsername());
        redisService.deleteRefreshToken(userDetails.getUsername());
    }

}
