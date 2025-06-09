package kr.co.apiserver.dto;

import kr.co.apiserver.domain.emums.UserRole;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class UserDto extends User {

    private String email;
    private String password;
    private String nickname;
    private boolean isSocial;
    private List<UserRole> roles = new ArrayList<>();

    public UserDto(String email, String password, String nickname, boolean isSocial, List<UserRole> roles) {
        super(
                email,
                password,
                roles.stream().map(str -> new SimpleGrantedAuthority(str.getAuthority())).toList());

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.isSocial = isSocial;
        this.roles = roles;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("nickname", nickname);
        claims.put("isSocial", isSocial);
        claims.put("roleNames", roles.stream().map(role -> role.name()).toList());
        return claims;
    }

    public static UserDto fromEntity(kr.co.apiserver.domain.User user) {
        return new UserDto(
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.isSocial(),
                user.getUserRoleList()
        );
    }

}
