package kr.co.apiserver.dto;

import jakarta.validation.constraints.NotBlank;
import kr.co.apiserver.domain.emums.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserInfoChangeRequestDto {
    @NotBlank
    private String email;
    private String password;
    private String nickname;
    private MultipartFile profileImage;
}
