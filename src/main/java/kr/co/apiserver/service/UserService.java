package kr.co.apiserver.service;

import kr.co.apiserver.dto.UserDto;
import kr.co.apiserver.dto.UserModifyRequestDto;

import java.util.Map;

public interface UserService {

    void modifyUser(UserModifyRequestDto requestDto);

    Map<String, Object> loginWithKakao(String code);

    Map<String, String> refreshAccessToken(String refreshToken);
}
