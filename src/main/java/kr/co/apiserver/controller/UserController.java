package kr.co.apiserver.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.co.apiserver.dto.UserDto;
import kr.co.apiserver.dto.UserModifyRequestDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.service.RedisService;
import kr.co.apiserver.service.UserService;
import kr.co.apiserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
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
    public Map<String, Object> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");
        if (refreshToken == null) {
            log.error("refresh token is null");
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Map<String,Object> tokens = userService.refreshAccessToken(refreshToken);

        return tokens;
    }

    @PostMapping("/auth/kakao")
    public Map<String,Object> kakaoCallback(@RequestBody Map<String, String> body) {
        return userService.loginWithKakao(body.get("authCode"));
    }

    @PatchMapping("/modify")
    public ApiResponse<Void> modifyUser(@Valid @RequestBody UserModifyRequestDto requestDto) {
        userService.modifyUser(requestDto);
        return ApiResponse.ok(null);
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal UserDto userDto) {
        log.info("UserController - logout: " + userDto.getEmail());
        redisService.deleteRefreshToken(userDto.getEmail());
    }

}
