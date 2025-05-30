package kr.co.apiserver.config;

import kr.co.apiserver.security.ApiLoginFailHandler;
import kr.co.apiserver.security.ApiLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("-------secrityFilterChain()-------");

        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        http.sessionManagement(httpSecuritySessionManagementConfigurer -> {
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER); // 세션을 사용하지 않음
        });

        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());

        http.formLogin((formLogin) ->
                formLogin
                        .loginPage("/api/member/login")
                        .successHandler(new ApiLoginSuccessHandler())
                        .failureHandler(new ApiLoginFailHandler())
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD","GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control" , "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
