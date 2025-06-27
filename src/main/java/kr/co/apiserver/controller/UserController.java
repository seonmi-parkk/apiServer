package kr.co.apiserver.controller;

import jakarta.validation.Valid;
import kr.co.apiserver.dto.UserDto;
import kr.co.apiserver.dto.UserInfoChangeRequestDto;
import kr.co.apiserver.dto.UserInfoResponseDto;
import kr.co.apiserver.dto.UserModifyRequestDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.security.UserDetailsImpl;
import kr.co.apiserver.service.RedisService;
import kr.co.apiserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

        Map<String, String> tokens = userService.refreshAccessToken(refreshToken);

        return ApiResponse.ok(tokens);
    }

    // 카카오 로그인
    @PostMapping("/auth/kakao")
    public ApiResponse<Map<String, Object>> kakaoCallback(@RequestBody Map<String, String> body) {
        Map<String, Object> result = userService.loginWithKakao(body.get("authCode"));
        return ApiResponse.ok(result);
    }

    // oauth로 가입시 회원 정보 추가 입
    @PatchMapping("/modify")
    public ApiResponse<Void> modifyUser(@Valid @RequestBody UserModifyRequestDto requestDto) {
        userService.modifyUser(requestDto);
        return ApiResponse.ok(null);
    }

    // 로그아웃
    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("UserController - logout: " + userDetails.getUsername());
        redisService.deleteRefreshToken(userDetails.getUsername());
    }

    // 마이페이지 회원 상세 정보
    @GetMapping
    public ApiResponse<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.ok(userService.getUserInfo(userDetails.getUsername()));
    }

    // 프로필 이미지 변경
    @PatchMapping("/profile")
    public ApiResponse<String> updateProfileImage(
            @RequestPart(required = false) MultipartFile file,
            @RequestPart(required = false) String isDefault,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String filename = userService.updateProfileImage(file, isDefault, userDetails.getUsername());
        return ApiResponse.ok(filename);
    }

    // 비밀번호 검증
    @PostMapping("/verify-password")
    public ApiResponse<Void> verifyPassword(String password, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean isMatch = userService.verifyPassword(password, userDetails.getUsername());

        if (!isMatch) {
            return ApiResponse.error(ErrorCode.PASSWORD_MISMATCH);
        }
        return ApiResponse.ok(null);
    }

    // 회원 정보 수정
    @PutMapping
    public ApiResponse<UserInfoChangeRequestDto> changeUserInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserInfoChangeRequestDto requestDto
    ) {
        userService.changeUserInfo(userDetails.getUsername(), requestDto);
        return ApiResponse.ok(requestDto);
    }
}
