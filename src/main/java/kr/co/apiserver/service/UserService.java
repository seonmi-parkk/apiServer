package kr.co.apiserver.service;

import jakarta.validation.Valid;
import kr.co.apiserver.dto.UserDto;
import kr.co.apiserver.dto.UserInfoChangeRequestDto;
import kr.co.apiserver.dto.UserInfoResponseDto;
import kr.co.apiserver.dto.UserModifyRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {

    void modifyUser(UserModifyRequestDto requestDto);

    Map<String, Object> loginWithKakao(String code);

    Map<String, String> refreshAccessToken(String refreshToken);

    UserInfoResponseDto getUserInfo(String username);

    void changeUserInfo(String username, @Valid UserInfoChangeRequestDto requestDto);

    boolean verifyPassword(String password, String username);


    String updateProfileImage(MultipartFile file, String isDefault, String username);
}
