package kr.co.apiserver.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class JwtCheckFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // priflight 요청
        if(method.equals("OPTIONS")){
            return true;
        }

        // 로그인 관련 요청 / 이미지 조회 경로
        if( path.startsWith("/user/login")
            || path.startsWith("/user/signup")
            || path.startsWith("/user/auth/kakao")
            || path.startsWith("/user/refresh")
            || path.startsWith("/user/check-nickname")
            || path.startsWith("/user/email-verification")
            || path.startsWith("/upload/")
            || path.startsWith("/products/view/")
            || path.startsWith("/products/") && method.equals("GET")
            || path.startsWith("/categories/list")
        ) {
            return true;
        }

        // false => check
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String authorizationHeader = request.getHeader("Authorization");

            String accessToken = authorizationHeader.substring(7);
            String username = jwtUtil.validateToken(accessToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("doFilterInternal : " + e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            // 에러코드
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
