package kr.co.apiserver.dto;

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
    private List<String> roleNames = new ArrayList<>();

    public UserDto(String email, String password, String nickname, boolean isSocial, List<String> roleNames) {
        super(
                email,
                password,
                roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_"+str)).toList());

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.isSocial = isSocial;
        this.roleNames = roleNames;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("password", password);
        claims.put("nickname", nickname);
        claims.put("isSocial", isSocial);
        claims.put("roleNames", roleNames);
        return claims;
    }

    public static UserDto fromEntity(kr.co.apiserver.domain.User user) {
        return new UserDto(
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.isSocial(),
                user.getUserRoleList()
                        .stream()
                        .map(role -> role.name())
                        .toList()
        );
    }

}
