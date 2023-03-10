package com.example.sns.config;

import com.example.sns.controller.RsData;
import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.ErrorResult;
import com.example.sns.jwt.JwtExceptionFilter;
import com.example.sns.jwt.JwtTokenFilter;
import com.example.sns.jwt.JwtTokenUtils;
import com.example.sns.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.sns.exception.ErrorCode.INVALID_PERMISSION;
import static com.example.sns.exception.ErrorCode.ROLE_FORBIDDEN;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    @Value("${jwt.token.secret}")
    private String secretKey;
    private final JwtTokenUtils jwtTokenUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeHttpRequests()
                .antMatchers(HttpMethod.GET, "/api/v1/posts/my, /api/v1/alarms").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                .antMatchers(HttpMethod.GET).permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/users/join", "/api/v1/users/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/users/**/role/change").hasRole("ROLE_ADMIN")
                .antMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
                .antMatchers(HttpMethod.PUT).authenticated()
                .antMatchers(HttpMethod.DELETE).authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtTokenFilter(userService, secretKey, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtTokenFilter.class)
                .exceptionHandling()
                // ?????? ?????? ??? INVALID_PERMISSION ?????? ??????
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        makeErrorResponse(response, INVALID_PERMISSION);
                    }
                })
                // ?????? ?????? ??? ROLE_FORBIDDEN ?????? ??????
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
                        makeErrorResponse(response, ROLE_FORBIDDEN);
                    }
                })
                .and()
                .build();
    }

    // Security Filter Chain?????? ???????????? Exception??? ExceptionManager ?????? ?????? ?????? ????????? ????????? ?????? ??????
    public void makeErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), RsData.error(new ErrorResult(errorCode, "???????????? ????????? ????????????.")));
    }

}
