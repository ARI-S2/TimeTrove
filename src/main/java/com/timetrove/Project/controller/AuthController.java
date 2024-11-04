package com.timetrove.Project.controller;

import com.timetrove.Project.common.config.auth.JwtProperties;
import com.timetrove.Project.dto.user.TokenDto;
import com.timetrove.Project.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 카카오 로그인 콜백 엔드포인트
     * @param code - 카카오 인증 서버에서 전달받은 인증 코드
     * @return 토큰 객체
     */
    @Operation(summary = "카카오 로그인", description = "카카오 OAuth2 인증을 수행하고, 인증된 사용자의 액세스&리프레시 토큰을 반환합니다.")
    @GetMapping("/login/oauth2/callback/kakao")
    public ResponseEntity<TokenDto> kakaoLogin(@RequestParam("code") String code) {
        TokenDto tokens = authService.authenticateUser(code);
        return createTokenResponse(tokens);
    }

    /**
     * @param refreshToken - 리프레시 토큰을 헤더에서 받아서 로그아웃 처리
     * @return 성공적으로 로그아웃되면 응답 내용이 없는 상태로 반환
     */
    @Operation(summary = "로그아웃", description = "사용자가 보유한 리프레시 토큰을 무효화하여 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param refreshToken - 만료되기 전의 리프레시 토큰을 받아 새로운 액세스 및 리프레시 토큰을 발급
     * @return 새로운 토큰 객체
     */
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 검증하고, 유효하다면 새로운 액세스&리프레시 토큰을 발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissueTokens(@RequestHeader("Authorization") String refreshToken) {
        TokenDto tokens = authService.reissueTokens(refreshToken);
        return createTokenResponse(tokens);
    }

    /**
     * 토큰 생성 메서드
     * @param tokens - 발급된 토큰 정보
     * @return 헤더에 토큰을 포함한 응답 객체
     */
    private ResponseEntity<TokenDto> createTokenResponse(TokenDto tokens) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING,
                JwtProperties.TOKEN_PREFIX + tokens.getAccessToken() + " " + tokens.getRefreshToken());
        return ResponseEntity.ok().headers(headers).body(tokens);
    }
}
