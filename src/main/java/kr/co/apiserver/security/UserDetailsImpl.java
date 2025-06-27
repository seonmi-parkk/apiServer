package kr.co.apiserver.security;

import io.jsonwebtoken.Claims;
import kr.co.apiserver.domain.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getUserRoleList().stream().map(roleName ->
                new SimpleGrantedAuthority("ROLE_" + roleName)).toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public Map<String, String> getUserInfo() {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", user.getEmail());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("isSocial", String.valueOf(user.isSocial()));
        userInfo.put("roleNames", user.getUserRoleList().stream()
                .map(Enum::name)
                .collect(Collectors.joining(",")));
        userInfo.put("profileImage", user.getProfileImage() != null ?
                user.getProfileImage() : "default-profile.png");
        return userInfo;
    }
}
