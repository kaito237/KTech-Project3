package com.example.KTech_Project3.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // log xử lý filter
        log.info("JwtTokenFilter: Starting request filtering...");

        //Lấy authorization header
        String authHeader
                = request.getHeader(HttpHeaders.AUTHORIZATION);

        // long token có thể là null
       log.info("Authorization Header: {}", authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
            String token = authHeader.substring(7);
            log.info("Extracted Token: {}",token);
            // 정당한 토큰인지 검사
            if (jwtTokenUtils.validate(token)) {
                // 정당하면 인증정보 객체에 담아서 인증 받은 사용자임을 확인
//                SecurityContext context = SecurityContextHolder.createEmptyContext();
                // 사용자 정보 회수
                String username = jwtTokenUtils
                        .parseClaims(token)
                        .getSubject();
                log.info("Username from Token: {}", username);

                UserDetails userDetails = manager.loadUserByUsername(username);
                log.info("UserDetails Loaded: {}", userDetails.getUsername());


                // 인증 정보 생성 및 권한 주입
                AbstractAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // kg xu dung mat khau cho xac thuc jwt
                        userDetails.getAuthorities()
                );
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);

                log.info("JWT security authentication set in context for user: {}", username);
            } else {
                log.warn("Invalid JWT token");
            }
            } catch (Exception e) {
                // Bắt các ngoại lệ liên quan đến token
                log.error("Error processing JWT token: {}", e.getMessage());
            }
        } else {
            if (authHeader == null) {
                log.warn("Authorization header is missing");
            } else {
                log.warn("Authorization header does not start with 'Bearer '");
            }
        }

        // Tiếp tục chuỗi filter
        filterChain.doFilter(request, response);
    }


}