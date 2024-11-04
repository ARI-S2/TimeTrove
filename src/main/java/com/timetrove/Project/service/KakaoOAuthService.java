package com.timetrove.Project.service;

import com.timetrove.Project.dto.user.KakaoProfile;
import com.timetrove.Project.dto.user.OauthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthService {
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    private static final String KAKAO_REDIRECT_URI = "http://localhost:3000/login/oauth2/callback/kakao";
    private static final String KAKAO_TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    /**
     * 카카오 액세스 토큰 요청 메서드
     * @param: String code - 카카오 인증 코드
     * @return 액세스 토큰 문자열
     */
    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        ResponseEntity<OauthToken> response = restTemplate.postForEntity(
                KAKAO_TOKEN_URI,
                new HttpEntity<>(params, headers),
                OauthToken.class
        );

        return response.getBody().getAccess_token();
    }

    /**
     * 카카오 프로필 정보 조회 메서드
     * @param: String token - 액세스 토큰
     * @return KakaoProfile 객체
     */
    public KakaoProfile findProfile(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<KakaoProfile> response = restTemplate.postForEntity(
                KAKAO_USER_INFO_URI,
                new HttpEntity<>(headers),
                KakaoProfile.class
        );

        return response.getBody();
    }
}