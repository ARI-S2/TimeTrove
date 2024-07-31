package com.timetrove.Project.common.config.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        System.out.println("CustomAuthenticationEntryPoint 진입");
    	String exception = (String) request.getAttribute(JwtProperties.HEADER_STRING);
        String errorCode;
        String[] excludePath = {"/board", "/login"};
        String path = request.getRequestURI();
        System.out.println(path);
        if(Arrays.stream(excludePath).anyMatch(path::startsWith)) {
        	System.out.println("excludePath 실행");
        	return;
        }
        if ("토큰이 만료되었습니다.".equals(exception)) {
            errorCode = "토큰이 만료되었습니다.";
            setResponse(response, errorCode);
        } else if ("유효하지 않은 토큰입니다.".equals(exception)) {
            errorCode = "유효하지 않은 토큰입니다.";
            setResponse(response, errorCode);
        }
    }

    private void setResponse(HttpServletResponse response, String errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JwtProperties.HEADER_STRING + " : " + errorCode);
    }
}
