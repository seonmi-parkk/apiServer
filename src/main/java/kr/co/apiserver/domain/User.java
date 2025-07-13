package kr.co.apiserver.domain;

import jakarta.persistence.*;
import kr.co.apiserver.domain.emums.UserRole;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "userRoleList")
public class User extends TimeBaseEntity {
    @Id
    private String email;

    @Column(nullable = false)
    private String password;

    private String nickname;

    private String profileImage;

    private boolean isSocial;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
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

    public void changeProfile(String profileImage) {
        this.profileImage = profileImage;
    }

    public void changeSocial(boolean isSocial) {
        this.isSocial = isSocial;
    }


}
