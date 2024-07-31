package com.timetrove.Project.controller;

import com.timetrove.Project.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.timetrove.Project.common.config.auth.JwtProperties;
import com.timetrove.Project.dto.user.OauthToken;
import com.timetrove.Project.dto.user.UserDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
    @GetMapping("/login/oauth2/callback/kakao")
    public ResponseEntity<Object> getLogin(@RequestParam("code") String code) {
        // 넘어온 인가 코드를 통해 access_token 발급
        OauthToken oauthToken = userService.getAccessToken(code);

        // 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장
        String jwtToken = userService.SaveUserAndGetToken(oauthToken.getAccess_token());
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
        //return ResponseEntity.ok().headers(headers).body("success");
        return ResponseEntity.ok().headers(headers).body(jwtToken);
        
    }

    // jwt 토큰으로 유저정보 요청하기
    @GetMapping("/user")
    public ResponseEntity<Object> getCurrentUser(@AuthenticationPrincipal Long userCode) {
        if (userCode == null) {
            return ResponseEntity.status(401).body("Unauthorized: No userCode found in request");
        }
        UserDto userDto = userService.getUserById(userCode);
        return ResponseEntity.ok().body(userDto);
    }
    
    
}
