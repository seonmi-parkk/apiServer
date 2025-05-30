package kr.co.apiserver.repository;

import kr.co.apiserver.domain.User;
import kr.co.apiserver.domain.UserRole;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Log4j2
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testInsertUser(){
        for (int i=10; i<14; i++) {
            User user = User.builder()
                    .email("user" + i + "@test.com")
                    .password(passwordEncoder.encode("1111"))
                    .nickname("user" + i)
                    .build();
            user.addRole(UserRole.USER);

            if(i >= 5) {
                user.addRole(UserRole.MANAGER);
            }

            if(i >= 8) {
                user.addRole(UserRole.ADMIN);
            }

            userRepository.save(user);
        }
    }

    @Test
    public void testRead(){

        String email = "user11@test.com";

        User user = userRepository.getWithRoles(email).orElseThrow();

        log.info("user : " +user);
        log.info("Roles : " + user.getUserRoleList());
    }
}
