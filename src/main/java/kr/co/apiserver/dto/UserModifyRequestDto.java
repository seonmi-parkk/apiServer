package kr.co.apiserver.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class UserModifyRequestDto {
    @NotBlank(message = "이메일은 필수값 입니다.")
    private String email;

    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,20}$",
            message = "비밀번호는 영문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "닉네임은 필수값 입니다.")
    private String nickname;
}
