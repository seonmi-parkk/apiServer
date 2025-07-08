package kr.co.apiserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.domain.emums.FileCategory;
import kr.co.apiserver.domain.emums.UserRole;
import kr.co.apiserver.dto.*;
import kr.co.apiserver.repository.UserRepository;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.util.CustomFileUtil;
import kr.co.apiserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final CustomFileUtil fileUtil;
    private final RedisService redisService;
    private final EmailVerificationService emailVerificationService;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${user.default-profile}")
    private String defaultProfile;

    @Transactional
    @Override
    public void modifyUser(UserModifyRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.changePassword(passwordEncoder.encode(requestDto.getPassword()));
        user.changeNickname(requestDto.getNickname());
        user.changeSocial(false);
    }

    @Override
    public Map<String, String> refreshAccessToken(String refreshToken) {
        // 토큰 유효성 검증
        String username = jwtUtil.validateToken(refreshToken);
        User user = userRepository.getWithRoles(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Map<String, String> tokens = jwtUtil.refreshAccessToken(refreshToken, user);
        redisService.saveRefreshToken(user.getEmail(), tokens.get("refreshToken"));
        return tokens;
    }

    @Override
    public UserInfoResponseDto getUserInfo(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserInfoResponseDto.fromEntity(user);
    }

    @Override
    public void changeUserInfo(String username, UserInfoChangeRequestDto requestDto) {

    }

    @Override
    public boolean verifyPassword(String password, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return passwordEncoder.matches(password, user.getPassword());
    }

    @Transactional
    @Override
    public String updateProfileImage(MultipartFile file, String isDefault, String username) {
        User user = userRepository.findById(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String previousProfile = user.getProfileImage();

        // 프로필 이미지 초기화
        if(isDefault != null && isDefault.equals("true")) {
            user.changeProfile(null);
            return defaultProfile;
        }

        // 새 프로필 이미지 저장
        String uploadFilename = fileUtil.saveFile(file, FileCategory.PROFILE);
        // 새 프로필 이미지 db 반영
        user.changeProfile(uploadFilename);

        // 기존 프로필 이미지 파일삭제
        if(previousProfile != null) {
            fileUtil.deleteFile(previousProfile, FileCategory.PROFILE);
        }

        return uploadFilename;
    }

    @Transactional
    @Override
    public void changePassword(String username, ChangePasswordRequestDto requestDto) {
        User user = userRepository.findById(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        user.changePassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    @Override
    public boolean isNicknameDuplicated(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    @Override
    public void changeNickname(String nickname, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 닉네임 중복 체크
        if(isNicknameDuplicated(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }

        user.changeNickname(nickname);
    }

    @Override
    public void sendVerificationEmail(String email) {
        // 이메일 중복 체크
        userRepository.findById(email).ifPresent(e -> {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        });

        // 이메일 인증 코드 전송
        emailVerificationService.sendCode(email);
    }

    @Override
    public boolean verifyEmailCode(EmailVerifyRequestDto requestDto) {
        String email = requestDto.getEmail();

        // 이메일 인증 코드 확인
        boolean valid = emailVerificationService.verifyCode(email, requestDto.getCode());

        // 인증 코드가 유효한 경우
        if (valid) {
            // redis에 인증된 이메일 저장
            emailVerificationService.clearCode(email);
            emailVerificationService.saveVerifiedEmail(email);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    @Override
    public Map<String,Object> loginWithKakao(String code) {
        // 인가 코드로 액세스 토큰 요청
        String accessToken = getAccessTokenFromKakao(code);

        // 사용자 정보 요청
        String email = getEmailFromKakao(accessToken);

        // 회원정보 조회
        Optional<User> result = userRepository.findById(email);

        // 기존 회원
        if(result.isPresent()) {
            // acceesssToken, refreshToken 생성
            return createTokens(result.get());
        }

        // 신규 회원 가입
        User socialUser = makeSocialUser(email);
        User savedUser = userRepository.save(socialUser);

        // acceesssToken, refreshToken 생성
        return createTokens(savedUser);
    }

    private String getAccessTokenFromKakao(String code){
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
        ResponseEntity<String> tokenResponse = new RestTemplate().postForEntity(tokenUri, tokenRequest, String.class);

        JsonNode tokenJson;
        try {
            tokenJson = objectMapper.readTree(tokenResponse.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }

        return tokenJson.get("access_token").asText();
    }

    private String getEmailFromKakao(String accessToken) {

        String kakaoGetUserUrl = "https://kapi.kakao.com/v2/user/me";

        if(accessToken == null) {
            throw new IllegalArgumentException("accessToken is null");
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/x-www-form-urlencoded;");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromUriString(kakaoGetUserUrl).build();

        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(
                uriBuilder.toString(), HttpMethod.GET, entity, LinkedHashMap.class);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        LinkedHashMap<String, String> kakakoAccount = bodyMap.get("kakao_account");

        return kakakoAccount.get("email");
    }

    private Map<String,Object> createTokens(User user) {
        Map<String,Object> claims = UserDto.fromEntity(user).getClaims();

        String username = user.getEmail();

        String jwtAccessToken = jwtUtil.createAccessToken(user);
        String jwtRefreshToken = jwtUtil.createRefreshToken(username);

        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        // Redis에 Refresh Token 저장
        redisService.saveRefreshToken(username, jwtRefreshToken);
        return claims;
    }

    private String makeTempPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append((char) ((Math.random() * 55) + 65));
        }
        return sb.toString();
    }

    private User makeSocialUser(String email){
        String tempPassword = makeTempPassword();
        String nickname = "소셜 회원";

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(tempPassword))
                .nickname(nickname)
                .isSocial(true)
                .build();
        user.addRole(UserRole.USER);

        return user;
    }
}
