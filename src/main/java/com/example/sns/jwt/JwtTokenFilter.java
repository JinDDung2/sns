package com.example.sns.jwt;

import com.example.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
@Slf4j

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    @Value("${jwt.token.secret}")
    private final String secretKey;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에 토큰없으면 거절
        String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (requestHeader == null || !requestHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 추출 실패 거절
        String token;
        try {
            token = requestHeader.split(" ")[1].trim();
        } catch (IllegalArgumentException e) {
            log.error("token이 비어있습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 경우 거절
        if (jwtTokenUtils.isExpired(token, secretKey)) {
            filterChain.doFilter(request, response);
            log.error("token 기간이 만료되었습니다. token={}", token);
            return;
        }

        String userName = jwtTokenUtils.getUserName(token, secretKey);

        // 통과
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
