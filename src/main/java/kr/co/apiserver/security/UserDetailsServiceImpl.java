package kr.co.apiserver.security;

import kr.co.apiserver.domain.User;
import kr.co.apiserver.dto.UserDto;
import kr.co.apiserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername : " + username);

        User user = userRepository.getWithRoles(username).orElseThrow(()->new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        UserDto userDto = UserDto.fromEntity(user);

        return userDto;
    }

}
