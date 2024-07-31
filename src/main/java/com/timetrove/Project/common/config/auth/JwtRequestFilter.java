package com.timetrove.Project.common.config.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.timetrove.Project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private UserRepository userRepository;
	
	@Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		System.out.println("shouldNotFilter 진입");
        String[] excludePath = {"/board", "/login"};
        System.out.println(request.getRequestURL());
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

        String token = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");
        Long userCode = null;
        List<String> roles = null;

        try {
            var decodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token);
            userCode = decodedJWT.getClaim("id").asLong();
            String rolesString = decodedJWT.getClaim("roles").asString();
            if (rolesString == "ROLE_USER") {
            	roles = Collections.singletonList("ROLE_USER");
            } else {
            	roles = Arrays.asList(rolesString.split(","));
            }
            
            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role))
                    .collect(Collectors.toList());

            CustomAuthenticationToken authentication = new CustomAuthenticationToken(userCode, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            request.setAttribute("userCode", userCode);

        } catch (TokenExpiredException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "토큰이 만료되었습니다.");
            setAnonymousAuthentication();
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "유효하지 않은 토큰입니다.");
            setAnonymousAuthentication();
        }

        filterChain.doFilter(request, response);
    }

    private void setAnonymousAuthentication() {
    	System.out.println("setAnonymousAuthentication 진입");
        SecurityContextHolder.getContext().setAuthentication(
            new AnonymousAuthenticationToken("anonymousUser", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))
        );
    }
}
