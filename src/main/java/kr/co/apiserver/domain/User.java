package kr.co.apiserver.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "userRoleList")
public class User {
    @Id
    private String email;
    private String password;
    private String nickname;
    private boolean isSocial;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private List<UserRole> userRoleList = new ArrayList<>();

    public void addRole(UserRole userRole) {
        this.userRoleList.add(userRole);
    }

    public void clearRole(){
        userRoleList.clear();
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeSocial(boolean isSocial) {
        this.isSocial = isSocial;
    }
}
