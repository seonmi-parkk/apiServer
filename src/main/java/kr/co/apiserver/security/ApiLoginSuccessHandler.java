package kr.co.apiserver.security;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.apiserver.dto.UserDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

@Log4j2
public class ApiLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess : " + authentication);

        UserDto userDto = (UserDto) authentication.getPrincipal();
        Map<String, Object> claims = userDto.getClaims();

        claims.put("accessToken", "");
        claims.put("refreshToken", "");

        Gson gson = new Gson();
        String jsonStr = gson.toJson(claims);

        response.setContentType("application/json; charset=UTF-8");

        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}
