package kr.co.apiserver.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.response.exception.CustomAuthenticationException;
import kr.co.apiserver.response.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomJwtEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // JWT를 사용하는 API 요청인 경우에만 401로 응답
        if (request.getRequestURI().startsWith("/user/refresh") || request.getHeader("Authorization") != null) {
            ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

            if (authException instanceof CustomAuthenticationException customEx) {
                errorCode = customEx.getErrorCode();
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            ApiResponse<Void> errorResponse = ApiResponse.error(errorCode);

            String json = new ObjectMapper().writeValueAsString(errorResponse);
            response.getWriter().write(json);
        } else {
            // 나머지는 기존 로그인 페이지로 리다이렉트
            response.sendRedirect("/user/login");
        }

    }
}