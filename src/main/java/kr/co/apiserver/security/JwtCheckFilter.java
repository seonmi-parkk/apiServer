package kr.co.apiserver.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.apiserver.dto.UserDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.response.exception.CustomAuthenticationException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class JwtCheckFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("check uri: " + path);

        if(path.startsWith("/user/") ) {
            return true;
        }

        // false == check
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("doFilterInternal" );

        try {
            String authorizationHeader = request.getHeader("Authorization");

            String accessToken = authorizationHeader.substring(7);
            Map<String, Object> claims = jwtUtil.validateToken(accessToken);
            log.info("claims : " + claims);

            String email = (String) claims.get("email");
            String password = (String) claims.get("password");
            String nickname = (String) claims.get("nickname");
            Boolean isSocial = (Boolean) claims.get("isSocial");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            UserDto userDto = new UserDto(email, password, nickname, isSocial, roleNames);

            log.info("-------------userDto : " + userDto);
            log.info("-------------userDto : " + userDto.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDto, password, userDto.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            // 에러코드 결정
            ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
            if (e instanceof CustomAuthenticationException customEx) {
                errorCode = customEx.getErrorCode();
            }

            ApiResponse<Void> errorResponse = ApiResponse.error(errorCode);
            String json = new ObjectMapper().writeValueAsString(errorResponse);
            response.getWriter().write(json);
        }

    }

}
