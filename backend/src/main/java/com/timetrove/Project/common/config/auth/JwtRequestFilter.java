package com.timetrove.Project.common.config.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

	@Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String[] excludePath = {"/api/login", "/api/reissue"};
        String path = request.getRequestURI();
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);

        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String accessToken = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "").trim();

            var decodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(accessToken);

            Long userCode = decodedJWT.getClaim("id").asLong();

            List<String> authorities = Arrays.asList(decodedJWT.getClaim("roles")
                    .toString()
                    .split(","));

            List<? extends GrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            SecurityContextHolder.getContext().setAuthentication(
                    new CustomAuthenticationToken(userCode, accessToken, simpleGrantedAuthorities));
        }
        catch (TokenExpiredException e) {
            log.info("토큰 만료 오류 출력: "+e);
            request.setAttribute("exception", e);
        }
        catch (JWTVerificationException | IllegalArgumentException e) {
            log.info("토큰 유효성 오류 출력: "+e);
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }
}
