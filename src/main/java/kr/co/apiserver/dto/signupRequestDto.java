package kr.co.apiserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.co.apiserver.validation.ValidPassword;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class signupRequestDto {
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @ValidPassword
    private String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;
}
