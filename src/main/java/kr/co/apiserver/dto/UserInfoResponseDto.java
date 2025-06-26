package kr.co.apiserver.dto;

import kr.co.apiserver.domain.User;
import lombok.Getter;

@Getter
public class UserInfoResponseDto {
    private String email;
    private String nickname;
    private String profileImage;

    public static UserInfoResponseDto fromEntity(User user) {
        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
        userInfoResponseDto.email = user.getEmail();
        userInfoResponseDto.nickname = user.getNickname();
        userInfoResponseDto.profileImage = user.getProfileImage();
        return userInfoResponseDto;
    }
}
