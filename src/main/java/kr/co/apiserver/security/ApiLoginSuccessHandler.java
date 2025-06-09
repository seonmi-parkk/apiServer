package kr.co.apiserver.security;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.apiserver.service.RedisService;
import kr.co.apiserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class ApiLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess : " + authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        //Map<String, Object> claims = userDto.getClaims();
        log.info("userDetails : " + userDetails);
        log.info("userDetails.getUser() : " + userDetails.getUser());
        String accessToken = jwtUtil.createAccessToken(userDetails.getUser());
        String refreshToken = jwtUtil.createRefreshToken(userDetails.getUsername());

        redisService.saveRefreshToken(userDetails.getUsername(), refreshToken);

        Map<String, String> userInfo = userDetails.getUserInfo();

        log.info("====== userInfo : " + userInfo);
        log.info("====== accessToken : " + accessToken);
        log.info("====== refreshToken : " + refreshToken);
        userInfo.put("accessToken", accessToken);
        userInfo.put("refreshToken", refreshToken);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(userInfo);

        response.setContentType("application/json; charset=UTF-8");

        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}
